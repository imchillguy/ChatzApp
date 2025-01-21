package com.chillguy.chatzapp.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chillguy.chatzapp.model.dto.ChatMessage

abstract class MessageViewHolder(private val mItemView: View): RecyclerView.ViewHolder(mItemView) {

    abstract fun bindItems(chatMessage: ChatMessage)

}