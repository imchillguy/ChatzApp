package com.chillguy.chatzapp.constants


class Constants {

    companion object {
        const val PLEASE_TRY_AGAIN = "Please try again"
        const val SOMETHING_WENT_WRONG = "Something went wrong"
        const val USER_CREATED_SUCCESSFULLY = "LoggedInUser created successfully. Please Login"
        const val PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully"
        const val SIGN_UP_SUCCESS = "signUpSuccess"
        const val SIGN_UP_MESSAGE = "signUpMessage"
        const val USER_LOGGED_IN_SUCCESSFULLY = "LoggedInUser Logged In Successfully"
        const val INCORRECT_EMAIL_OR_PASSWORD = "Incorrect email or password"
    }


    class UserConstants {
        companion object {
            const val USER = "user"
            const val USER_ID = "userId"
            const val USER_NAME = "userName"
            const val DOCUMENT_ID = "documentId"
            const val USER_EMAIL = "userEmail"
            const val USER_STATUS = "userStatus"
            const val USER_IMAGE_URL = "userImageUrl"
            const val USER_PROFILE_ID = "userProfileId"
            const val DEFAULT_USER_ID = "defaultUserId"
            const val DEFAULT_USER_IMAGE_URL = "https://cdn.pixabay.com/photo/2012/04/26/19/43/profile-42914_1280.png"
        }
    }

    class LoggedInUserConstants {
        companion object {
            const val LOGGED_IN_USER_ID = "loggedInUserId"
            const val LOGGED_IN_USER_DOCUMENT_ID = "loggedInUserDocumentId"
            const val LOGGED_IN_USER_NAME = "loggedInUserName"
            const val LOGGED_IN_USER_IMAGE_URL = "loggedInUserImageUrl"
        }
    }

    class ContactConstants {
        companion object {
            const val CONTACT_USER_ID = "contactUserId"
            const val CONTACT_USER_NAME = "contactUserName"
            const val CONTACT_USER_STATUS = "contactUserStatus"
            const val CONTACT_USER_IMAGE_URL = "contactUserImageUrl"
        }
    }

    class CloudinaryConstants {
        companion object {
            const val CLOUD_NAME = "cloud_name"
            const val CLOUD_NAME_VALUE = "dxhfn2bfu"
            const val SECURE_URL = "secure_url"
        }
    }

    class MessageConstants {
        companion object {
            const val MESSAGE = "message"
            const val CHATS = "chats"
            const val SENDER = "sender"
            const val RECEIVER = "receiver"
            const val TIME_STAMP = "timeStamp"
        }
    }

    class RecentChatConstants {
        companion object {
            const val MESSAGE = "message"
            const val SENDER = "sender"
            const val RECEIVER = "receiver"
            const val TIME_STAMP = "timeStamp"
            const val FRIEND_NAME = "friendName"
            const val FRIEND_IMAGE_URL = "friendImageUrl"
            const val CONVERSATION = "conversation"
            const val RECENT_CHAT = "recentChat"
            const val FRIEND_STATUS = "friendStatus"
        }
    }

    class TokenConstants {
        companion object {
            const val TOKENS = "Tokens"
            const val TOKEN = "token"
        }
    }
}