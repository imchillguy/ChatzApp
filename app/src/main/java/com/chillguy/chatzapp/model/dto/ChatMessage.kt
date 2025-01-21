package com.chillguy.chatzapp.model.dto

import com.chillguy.chatzapp.enums.ChatType

data class ChatMessage(
    val id: String,
    val msgText: String,
    val msgTime: String,
    val type: ChatType
)
