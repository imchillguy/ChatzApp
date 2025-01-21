package com.chillguy.chatzapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.enums.ContactStatus
import com.chillguy.chatzapp.model.response.SignUpResponse
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel() {

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val signUpResponse = MutableLiveData<SignUpResponse>()

    private val auth : FirebaseAuth by lazy { Firebase.auth }
    private val store: FirebaseFirestore by lazy { Firebase.firestore }

    fun registerUser(){
        signUpResponse.value = SignUpResponse.Loading
        auth.createUserWithEmailAndPassword(email.value?:"", password.value?:"")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    viewModelScope.launch(Dispatchers.IO) {
                        user?.let { storeUserData(user) }
                    }

                } else {
                    signUpResponse.value = SignUpResponse.Error(
                        errorMsg = Constants.PLEASE_TRY_AGAIN
                    )
                }
            }
    }

    private suspend fun storeUserData(user: FirebaseUser) {
        val userData = hashMapOf<String, Any?>(
            Constants.UserConstants.USER_ID to user.uid,
            Constants.UserConstants.USER_NAME to name.value,
            Constants.UserConstants.USER_EMAIL to email.value,
            Constants.UserConstants.USER_STATUS to ContactStatus.DEFAULT,
            Constants.UserConstants.USER_IMAGE_URL to Constants.UserConstants.DEFAULT_USER_IMAGE_URL
        )

        store.collection(Constants.UserConstants.USER)
            .add(userData)
            .addOnSuccessListener {
                signUpResponse.value = SignUpResponse.Success(
                    successMsg = Constants.USER_CREATED_SUCCESSFULLY
                )
            }
            .addOnFailureListener {
                signUpResponse.value = SignUpResponse.Error(
                    errorMsg = Constants.PLEASE_TRY_AGAIN
                )
            }
    }

}