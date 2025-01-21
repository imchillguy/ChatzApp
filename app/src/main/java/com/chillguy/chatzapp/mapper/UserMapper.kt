package com.chillguy.chatzapp.mapper

import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.model.dto.LoggedInUser

object UserMapper {

    fun HashMap<String, Any>.mapToUser(): LoggedInUser {
        val loggedInUser = LoggedInUser(
            name = this.getOrDefault(Constants.UserConstants.USER_NAME, "") as String,
            imageUrl = this.getOrDefault(Constants.UserConstants.USER_IMAGE_URL, Constants.UserConstants.DEFAULT_USER_IMAGE_URL) as String,
            userId = this.getOrDefault(Constants.UserConstants.USER_ID, "") as String,
            documentId = this.getOrDefault(Constants.UserConstants.DOCUMENT_ID, "") as String,
        )

        return loggedInUser
    }
}