package com.chillguy.chatzapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillguy.chatzapp.ChatzApplication
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.enums.ChatType
import com.chillguy.chatzapp.mapper.MessageMapper.toHashMap
import com.chillguy.chatzapp.mapper.RecentChatMapper.toHashMap
import com.chillguy.chatzapp.model.dto.ChatMessage
import com.chillguy.chatzapp.model.dto.Contacts
import com.chillguy.chatzapp.model.dto.Message
import com.chillguy.chatzapp.model.dto.Notification
import com.chillguy.chatzapp.model.dto.PushNotification
import com.chillguy.chatzapp.model.dto.RecentChat
import com.chillguy.chatzapp.model.dto.NotificationMessage
import com.chillguy.chatzapp.model.dto.LoggedInUser
import com.chillguy.chatzapp.network.ApiInstance
import com.chillguy.chatzapp.network.apiservice.NotificationApi
import com.chillguy.chatzapp.sessionmanager.SessionManager.Companion.getLoggedInUserId
import com.chillguy.chatzapp.utils.Utils
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"
    }

    val msg = MutableLiveData<String>()
    val chatMessageList = MutableLiveData<List<ChatMessage>>()
    val loggedInUser = MutableLiveData<LoggedInUser>()
    val chatContactUser = MutableLiveData<Contacts>()

    private val store: FirebaseFirestore by lazy { Firebase.firestore }

    fun getMessages(contactUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val loggedInUserId = ChatzApplication.mApplicationContext.getLoggedInUserId() ?: ""
            val chatId = listOf(loggedInUserId,contactUserId).sorted().joinToString(",")

            store.collection(Constants.MessageConstants.MESSAGE)
                .document(chatId)
                .collection(Constants.MessageConstants.CHATS)
                .orderBy(Constants.MessageConstants.TIME_STAMP, Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (value != null && !value.isEmpty && error == null) {
                        val messageList = mutableListOf<ChatMessage>()
                        value.forEach { document ->
                            val message = document.data.getOrDefault(Constants.MessageConstants.MESSAGE, "") as String
                            val sender = document.data.getOrDefault(Constants.MessageConstants.SENDER, "") as String
                            val receiver = document.data.getOrDefault(Constants.MessageConstants.RECEIVER, "") as String
                            val timeStamp = document.data.getOrDefault(Constants.MessageConstants.TIME_STAMP, "") as String
                            if (message.isNotEmpty()) {
                                val chat = ChatMessage(id = "${sender}_${timeStamp}_${receiver}", msgText = message, msgTime = Utils.getTimeWithoutSeconds(timeStamp) ,type = getChatType(sender, loggedInUserId))
                                messageList.add(chat)
                            }
                        }
                        chatMessageList.postValue(messageList)
                    }
                }

        }
    }

    private fun getChatType(sender: String, loggedInUserId: String): ChatType {
        return when {
            sender == loggedInUserId -> ChatType.SENDER
            else -> ChatType.RECEIVER
        }
    }

    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.loggedInUser.value = loggedInUser
    }

    fun setChatContactUser(chatContactUser: Contacts) {
        this.chatContactUser.value = chatContactUser
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch(Dispatchers.IO) {
            val chatId = listOf(message.senderId, message.receiverId)
                .sorted()
                .joinToString(",")

            val messageMap = message.toHashMap()

            store.collection(Constants.MessageConstants.MESSAGE)
                .document(chatId)
                .collection(Constants.MessageConstants.CHATS)
                .document(message.timeStamp)
                .set(messageMap)
                .addOnSuccessListener {

                    // store recent chat
                    val senderRecentChat = RecentChat(
                        senderId = message.senderId,
                        receiverId = message.receiverId,
                        message = "You: ${message.message}",
                        friendName = chatContactUser.value?.name?:"",
                        friendImageUrl = chatContactUser.value?.imageUrl?:"",
                        timeStamp = message.timeStamp
                    )

                    val senderConversationId = "${Constants.RecentChatConstants.CONVERSATION}${message.senderId}"
                    store.collection(Constants.MessageConstants.CHATS)
                        .document(senderConversationId)
                        .collection(Constants.RecentChatConstants.RECENT_CHAT)
                        .document(message.receiverId)
                        .set(senderRecentChat.toHashMap())

                    // update recent chat of receiver
                    val receiverRecentChat = RecentChat(
                        senderId = message.receiverId,
                        receiverId = message.senderId,
                        message = "${loggedInUser.value?.name?:""}: ${message.message}",
                        friendName = loggedInUser.value?.name?:"",
                        friendImageUrl = loggedInUser.value?.imageUrl?:Constants.UserConstants.DEFAULT_USER_IMAGE_URL,
                        timeStamp = message.timeStamp
                    )

                    val receiverConversationId = "${Constants.RecentChatConstants.CONVERSATION}${message.receiverId}"
                    store.collection(Constants.MessageConstants.CHATS)
                        .document(receiverConversationId)
                        .collection(Constants.RecentChatConstants.RECENT_CHAT)
                        .document(message.senderId)
                        .set(receiverRecentChat.toHashMap())

                    // send notification to receiver
                    store.collection(Constants.TokenConstants.TOKENS)
                        .document(message.receiverId)
                        .addSnapshotListener { value, error ->
                            if (value != null && value.exists()) {
                                val receiverToken = value.data?.getOrDefault(Constants.TokenConstants.TOKEN,"") as String

                                val notificationMessage = NotificationMessage(
                                    message = PushNotification(
                                        token = receiverToken,
                                        notification = Notification(
                                            body = message.message,
                                            title = loggedInUser.value?.name?:""
                                        )
                                    )
                                )

                                sendNotificationMessage(notificationMessage)
                            }
                        }
                }
        }
    }

    private fun sendNotificationMessage(notificationMessage: NotificationMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ApiInstance.getApiService(NotificationApi::class.java).pushNotification(notificationMessage)
            } catch (e: Exception) {
                Log.e(TAG, "sendNotificationMessage: ${e.message}", )
            }
        }
    }

}