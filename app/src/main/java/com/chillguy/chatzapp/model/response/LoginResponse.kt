package com.chillguy.chatzapp.model.response

sealed class LoginResponse {

    data object Loading: LoginResponse()

    data class Success(val successMsg: String): LoginResponse()

    data class Error(val errorMsg: String): LoginResponse()
}