package com.chillguy.chatzapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.fragments.HomeFragment
import com.chillguy.chatzapp.fragments.LoginFragment
import com.chillguy.chatzapp.listener.ISaveTokenListener
import com.chillguy.chatzapp.sessionmanager.SessionManager.Companion.getLoggedInUserId
import com.cloudinary.android.MediaManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ISaveTokenListener {
    private val auth : FirebaseAuth by lazy { Firebase.auth }
    private val store: FirebaseFirestore by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        initCloudinary()
        setContentView(R.layout.activity_main)
        if(auth.currentUser != null) {
            saveTokenToServer()
            navigateToHomeFragment()
        } else {
            navigateToLoginFragment()
        }
    }

    private fun navigateToLoginFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.root_container, LoginFragment())
            .commit()
    }

    private fun navigateToHomeFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.root_container, HomeFragment())
            .commit()
    }

    private fun initCloudinary(){
        val config = hashMapOf(Constants.CloudinaryConstants.CLOUD_NAME to Constants.CloudinaryConstants.CLOUD_NAME_VALUE)
        MediaManager.init(this, config)
    }

    override fun saveTokenToServer() {
        lifecycleScope.launch(Dispatchers.IO) {
            this@MainActivity.getLoggedInUserId()?.let { loggedInUserId ->
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token->

                    val tokenMap = hashMapOf(
                        Constants.TokenConstants.TOKEN to token
                    )

                    store.collection(Constants.TokenConstants.TOKENS)
                        .document(loggedInUserId)
                        .set(tokenMap)
                        .addOnSuccessListener {

                        }
                }
            }
        }
    }

}