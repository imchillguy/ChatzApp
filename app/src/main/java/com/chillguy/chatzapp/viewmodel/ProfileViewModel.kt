package com.chillguy.chatzapp.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillguy.chatzapp.ChatzApplication
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.model.dto.LoggedInUser
import com.chillguy.chatzapp.model.response.ProfileResponse
import com.chillguy.chatzapp.sessionmanager.SessionManager
import com.chillguy.chatzapp.sessionmanager.SessionManager.Companion.getLoggedInUserId
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileViewModel : ViewModel() {

    val name = MutableLiveData<String>("")
    val loggedInUser = MutableLiveData<LoggedInUser>()
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val store: FirebaseFirestore by lazy { Firebase.firestore }

    val profileUpdateResponse = MutableLiveData<ProfileResponse>()
    private val profileImageUpdateUri = MutableLiveData<Uri>()

    init {
        //getUserName()
    }

    private fun getUserName() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = ChatzApplication.mApplicationContext.getLoggedInUserId()
            store.collection(Constants.UserConstants.USER)
                .get()
                .addOnSuccessListener { result ->
                    val userData = result.filter { document ->
                        document.data.getOrDefault(
                            Constants.UserConstants.USER_ID,
                            ""
                        ) == userId
                    }

                    if (userData.isNotEmpty()) {
                        val userName = userData[0].data.getOrDefault(
                            Constants.UserConstants.USER_NAME,
                            ""
                        ) as String
                        name.postValue(userName)
                    }
                }
                .addOnFailureListener {
                    name.postValue("")
                }
        }
    }

    fun setProfileImageUpdateUri(uri: Uri) {
        profileImageUpdateUri.value = uri
    }

    fun updateProfile() {
        profileUpdateResponse.value = ProfileResponse.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val userProfileMap = mutableMapOf<String, String>()
            var userImageUrl = loggedInUser.value?.imageUrl
            val userName = name.value
            userName?.let {  name ->
                if (name.isNotEmpty()) {
                    userProfileMap[Constants.UserConstants.USER_NAME] = name
                }
            }

            profileImageUpdateUri.value?.let {
                MediaManager.get().upload(profileImageUpdateUri.value).unsigned("YOUR_KEY")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {}

                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

                        }

                        override fun onSuccess(
                            requestId: String?,
                            resultData: MutableMap<Any?, Any?>?
                        ) {
                            userImageUrl = resultData?.get(Constants.CloudinaryConstants.SECURE_URL) as? String
                            userImageUrl?.let {  imageUrl ->
                                if (imageUrl.isNotEmpty()) {
                                    userProfileMap[Constants.UserConstants.USER_IMAGE_URL] = imageUrl
                                }
                            }
                            updateProfile(userProfileMap)
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {}

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                    }).dispatch()
            } ?: run {
                updateProfile(userProfileMap)
            }
        }
    }

    private fun updateProfile(userProfileMap: Map<String, String>) {
        val documentId = loggedInUser.value?.documentId
        if (documentId?.isNotEmpty() == true) {
            store.collection(Constants.UserConstants.USER)
                .document(documentId)
                .update(userProfileMap)
                .addOnSuccessListener {
                    profileUpdateResponse.value = ProfileResponse.Success(
                        successMsg = Constants.PROFILE_UPDATED_SUCCESSFULLY
                    )
                }
                .addOnFailureListener {
                    profileUpdateResponse.value = ProfileResponse.Error(
                        errorMsg = Constants.SOMETHING_WENT_WRONG
                    )
                }
        }
    }

    fun setUserName(userName: String) {
        name.value = userName
    }

    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.loggedInUser.value = loggedInUser
    }

}