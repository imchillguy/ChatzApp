package com.chillguy.chatzapp.viewholder

import android.view.View
import android.widget.TextView
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.model.dto.ChatMessage

class ReceiverMessageViewHolder(private val mItemView: View): MessageViewHolder(mItemView) {

    private lateinit var msgText: TextView
    private lateinit var msgTime: TextView

    init {
        with (mItemView) {
            msgText = findViewById(R.id.receiverMsgText)
            msgTime = findViewById(R.id.receiverMsgTime)
        }
    }

    override fun bindItems(chatMessage: ChatMessage) {
        msgText.text = chatMessage.msgText
        msgTime.text = chatMessage.msgTime
    }
}