package com.chillguy.chatzapp.model.dto

import com.chillguy.chatzapp.utils.Utils

data class Message(
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timeStamp: String = Utils.getCurrentTimeStamp()
)
