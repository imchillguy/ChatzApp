package com.chillguy.chatzapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput

class ChatzNotificationReplyReceiver: BroadcastReceiver() {
    override fun onReceive(conttext: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        remoteInput?.let { bundle ->
            val text = bundle.getString(ChatzFirebaseService.KEY_TEXT_REPLY)

        }
    }
}