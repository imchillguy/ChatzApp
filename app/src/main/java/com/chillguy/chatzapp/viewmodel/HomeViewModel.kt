package com.chillguy.chatzapp.viewmodel

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillguy.chatzapp.ChatzApplication
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.enums.ContactStatus
import com.chillguy.chatzapp.mapper.ContactsMapper.mapToContacts
import com.chillguy.chatzapp.mapper.RecentChatMapper.mapToRecentChat
import com.chillguy.chatzapp.mapper.UserMapper.mapToUser
import com.chillguy.chatzapp.model.dto.Contacts
import com.chillguy.chatzapp.model.dto.RecentChat
import com.chillguy.chatzapp.model.dto.LoggedInUser
import com.chillguy.chatzapp.sessionmanager.SessionManager.Companion.getLoggedInUserId
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val store: FirebaseFirestore by lazy { Firebase.firestore }
    val loggedOut = MutableLiveData<Boolean>(false)
    val loggedInUser = MutableLiveData<LoggedInUser>()
    val contactsList = MutableLiveData<List<Contacts>>()
    val loggedInUserRecentChatList = MutableLiveData<List<RecentChat>>()

    init {
        getContactsData()
        getRecentChatsData()
    }

    fun updateStatus(status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = ChatzApplication.mApplicationContext.getLoggedInUserId()
            var documentId = ""
            store.collection(Constants.UserConstants.USER)
                .get()
                .addOnSuccessListener { result ->
                    result.forEach { document ->
                        if (document.data.getOrDefault(
                                Constants.UserConstants.USER_ID,
                                ""
                            ) == userId
                        ) {
                            documentId = document.id
                            store.collection(Constants.UserConstants.USER)
                                .document(documentId)
                                .update(Constants.UserConstants.USER_STATUS, status)
                            return@forEach
                        }
                    }
                }
        }
    }

    fun logOut() {
        auth.signOut()
        loggedOut.value = true
    }

    fun getLoggedInUserBundle(): Bundle {
        val loggedInUserData = loggedInUser.value
        var bundle = bundleOf()
        loggedInUserData?.let { userData ->
            bundle = bundleOf(
                Constants.LoggedInUserConstants.LOGGED_IN_USER_NAME to userData.name,
                Constants.LoggedInUserConstants.LOGGED_IN_USER_IMAGE_URL to userData.imageUrl,
                Constants.LoggedInUserConstants.LOGGED_IN_USER_ID to userData.userId,
                Constants.LoggedInUserConstants.LOGGED_IN_USER_DOCUMENT_ID to userData.documentId
            )
        }
        return bundle
    }

    fun getFriendsStatus(friendsId: String): ContactStatus {
        val friendsContactDetails = contactsList.value?.filter { contacts ->
            contacts.userId == friendsId
        }
        if (!friendsContactDetails.isNullOrEmpty()) {
            return friendsContactDetails[0].status
        }
        return ContactStatus.DEFAULT
    }

    private fun getContactsData() {
        viewModelScope.launch(Dispatchers.IO) {
            val loggedInUserId = ChatzApplication.mApplicationContext.getLoggedInUserId()
            store.collection(Constants.UserConstants.USER)
                .addSnapshotListener { value, error ->
                    if (value != null && !value.isEmpty) {
                        val contactsList = mutableListOf<Contacts>()
                        for (document in value) {
                            val userId = document.data.getOrDefault(
                                Constants.UserConstants.USER_ID,
                                ""
                            ) as String
                            if (userId != loggedInUserId) {
                                contactsList.add((document.data as HashMap<String, Any>).mapToContacts())
                            } else {
                                val loggedInUserMap = (document.data as HashMap<String, Any>)
                                loggedInUserMap[Constants.UserConstants.DOCUMENT_ID] = document.id
                                loggedInUser.postValue(loggedInUserMap.mapToUser())
                            }
                        }
                        this@HomeViewModel.contactsList.postValue(contactsList)
                    }
                }
        }
    }

    private fun getRecentChatsData() {
        viewModelScope.launch(Dispatchers.IO) {
            val conversationId =
                "${Constants.RecentChatConstants.CONVERSATION}${ChatzApplication.mApplicationContext.getLoggedInUserId()}"
            store.collection(Constants.MessageConstants.CHATS)
                .document(conversationId)
                .collection(Constants.RecentChatConstants.RECENT_CHAT)
                .addSnapshotListener { value, error ->
                    if (value != null && !value.isEmpty && error == null) {
                        val recentChatList = mutableListOf<RecentChat>()
                        value.forEach { recentChat ->
                            recentChatList.add((recentChat.data as HashMap<String, Any>).mapToRecentChat())
                        }
                        loggedInUserRecentChatList.postValue(recentChatList)
                    }
                }
        }
    }

}