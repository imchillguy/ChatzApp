package com.chillguy.chatzapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillguy.chatzapp.ChatzApplication
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.model.response.LoginResponse
import com.chillguy.chatzapp.sessionmanager.SessionManager
import com.chillguy.chatzapp.sessionmanager.SessionManager.Companion.setLoggedInUserId
import com.chillguy.chatzapp.store.DataStoreManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel: ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val loginResponse = MutableLiveData<LoginResponse>()

    private val auth : FirebaseAuth by lazy {
        Firebase.auth
    }

    fun loginUser() {
        loginResponse.value = LoginResponse.Loading
        viewModelScope.launch(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email.value?:"", password.value?:"")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = task.result.user?.uid
                        ChatzApplication.mApplicationContext.setLoggedInUserId(userId)
                        loginResponse.postValue(
                            LoginResponse.Success(
                                successMsg = Constants.USER_LOGGED_IN_SUCCESSFULLY
                            )
                        )
                    } else {
                        loginResponse.postValue(
                            LoginResponse.Error(
                                errorMsg = Constants.INCORRECT_EMAIL_OR_PASSWORD
                            )
                        )
                    }
                }
        }
    }

}