package com.chillguy.chatzapp.model.dto

import com.chillguy.chatzapp.enums.ContactStatus

data class RecentChat(
    val senderId: String,
    val receiverId: String,
    val friendName: String,
    val message: String,
    val friendImageUrl: String,
    val timeStamp: String
)
