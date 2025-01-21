package com.chillguy.chatzapp.model.dto

import com.chillguy.chatzapp.enums.ContactStatus

data class Contacts(
    val userId : String,
    val name: String,
    val imageUrl: String,
    val status: ContactStatus = ContactStatus.DEFAULT
)
