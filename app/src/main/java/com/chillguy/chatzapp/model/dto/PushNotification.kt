package com.chillguy.chatzapp.model.dto

data class PushNotification(
    val token: String,
    val notification: Notification
)
