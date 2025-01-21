package com.chillguy.chatzapp.model.dto

data class LoggedInUser(
    val name: String,
    val imageUrl: String,
    val userId: String,
    val documentId: String
)
