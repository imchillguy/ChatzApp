package com.chillguy.chatzapp.model.response

sealed class SignUpResponse {

    data object Loading: SignUpResponse()

    data class Success(val successMsg: String): SignUpResponse()

    data class Error(val errorMsg: String): SignUpResponse()

}