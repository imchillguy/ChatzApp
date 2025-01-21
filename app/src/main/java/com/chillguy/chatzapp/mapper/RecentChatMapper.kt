package com.chillguy.chatzapp.mapper

import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.enums.ContactStatus
import com.chillguy.chatzapp.model.dto.RecentChat

object RecentChatMapper {

    fun RecentChat.toHashMap(): HashMap<String, String> {
        val map = hashMapOf(
            Constants.RecentChatConstants.SENDER to senderId,
            Constants.RecentChatConstants.RECEIVER to receiverId,
            Constants.RecentChatConstants.MESSAGE to message,
            Constants.RecentChatConstants.FRIEND_NAME to friendName,
            Constants.RecentChatConstants.FRIEND_IMAGE_URL to friendImageUrl,
            Constants.RecentChatConstants.TIME_STAMP to timeStamp,
        )
        return map
    }

    fun HashMap<String, Any>.mapToRecentChat(): RecentChat {
        val recentChat = RecentChat(
            senderId = this.getOrDefault(Constants.RecentChatConstants.SENDER, "") as String,
            receiverId = this.getOrDefault(Constants.RecentChatConstants.RECEIVER, "") as String,
            friendName = this.getOrDefault(Constants.RecentChatConstants.FRIEND_NAME, "") as String,
            friendImageUrl = this.getOrDefault(Constants.RecentChatConstants.FRIEND_IMAGE_URL, Constants.UserConstants.DEFAULT_USER_IMAGE_URL) as String,
            message = this.getOrDefault(Constants.RecentChatConstants.MESSAGE, "") as String,
            timeStamp = this.getOrDefault(Constants.RecentChatConstants.TIME_STAMP, "") as String
        )
        return recentChat
    }

}