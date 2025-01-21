package com.chillguy.chatzapp.mapper

import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.enums.ContactStatus
import com.chillguy.chatzapp.model.dto.Contacts

object ContactsMapper {

    fun HashMap<String, Any>.mapToContacts(): Contacts {

        val contacts = Contacts(
            userId = this.getOrDefault(Constants.UserConstants.USER_ID, "") as String,
            name = this.getOrDefault(Constants.UserConstants.USER_NAME, "") as String,
            imageUrl = this.getOrDefault(Constants.UserConstants.USER_IMAGE_URL, Constants.UserConstants.DEFAULT_USER_IMAGE_URL) as String,
            status = (this.getOrDefault(Constants.UserConstants.USER_STATUS, ContactStatus.DEFAULT.status) as String).toContactsStatus()
        )
        return contacts
    }

    fun String.toContactsStatus(): ContactStatus {
        return when(this) {
            ContactStatus.ONLINE.status -> ContactStatus.ONLINE
            ContactStatus.OFFLINE.status -> ContactStatus.OFFLINE
            ContactStatus.DEFAULT.status -> ContactStatus.DEFAULT
            else -> ContactStatus.DEFAULT
        }
    }

}