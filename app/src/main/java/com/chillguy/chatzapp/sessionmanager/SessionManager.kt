package com.chillguy.chatzapp.sessionmanager

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillguy.chatzapp.ChatzApplication
import com.chillguy.chatzapp.store.DataStoreManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SessionManager {

    companion object {
        private val auth: FirebaseAuth by lazy { Firebase.auth }
        private var loggedInUserId: String? = null

        fun Context.setLoggedInUserId(userId: String?) {
            this@Companion.loggedInUserId = userId
            userId?.let {
                val coroutineScope = CoroutineScope(Dispatchers.IO)
                coroutineScope.launch(Dispatchers.IO) {
                    DataStoreManager(this@setLoggedInUserId).writeData(DataStoreManager.LOGGED_IN_USER_ID_PREF_KEY, userId)
                }
            }
        }

        fun Context.getLoggedInUserId()= runBlocking {
            if (!loggedInUserId.isNullOrEmpty()) {
                return@runBlocking loggedInUserId
            }

            val userId = DataStoreManager(this@getLoggedInUserId).readData(DataStoreManager.LOGGED_IN_USER_ID_PREF_KEY, null)
            userId?.let {
                loggedInUserId = it
                return@runBlocking  loggedInUserId
            } ?: run {
                loggedInUserId = auth.currentUser?.uid
                return@runBlocking loggedInUserId
            }
        }
    }

}