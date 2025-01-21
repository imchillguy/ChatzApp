package com.chillguy.chatzapp.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.chillguy.chatzapp.model.dto.ChatMessage

class ChatMessageDiffUtilCallback: DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }

}