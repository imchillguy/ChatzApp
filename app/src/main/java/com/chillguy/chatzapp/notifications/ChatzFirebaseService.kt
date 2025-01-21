package com.chillguy.chatzapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.chillguy.chatzapp.MainActivity
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.sessionmanager.SessionManager.Companion.getLoggedInUserId
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatzFirebaseService: FirebaseMessagingService() {

    private val store: FirebaseFirestore by lazy { Firebase.firestore }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "ChillGuyChatz"
        const val NOTIFICATION_CHANNEL_NAME = "Chat"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Chatz App"
        const val NOTIFICATION_ID = 1234
        const val KEY_TEXT_REPLY = "chatz_key_text_reply"
        const val REPLY = "Reply"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        createNotification(message)

    }

    private fun createNotification(message: RemoteMessage) {

        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel(REPLY)
            .build()

        val replyIntent = Intent(this, ChatzNotificationReplyReceiver::class.java)

        val replyPendingIntent = PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_MUTABLE)

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.send_icon,
            REPLY,
            replyPendingIntent
        ).addRemoteInput(remoteInput)
            .build()

        val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setSmallIcon(R.drawable.chatz_splash)
            .setAutoCancel(true)
            .addAction(replyAction)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = NOTIFICATION_CHANNEL_DESCRIPTION
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToServer(token)
    }

    private fun saveTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            this@ChatzFirebaseService.applicationContext.getLoggedInUserId()?.let { loggedInUserId ->
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