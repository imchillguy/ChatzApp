package com.chillguy.chatzapp.model.response

sealed class ProfileResponse {

    data object Loading: ProfileResponse()

    data class Success(val successMsg: String): ProfileResponse()

    data class Error(val errorMsg: String): ProfileResponse()
}