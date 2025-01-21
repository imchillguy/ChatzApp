package com.chillguy.chatzapp.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.chillguy.chatzapp.model.dto.RecentChat

class RecentChatDiffUtilCallback: DiffUtil.ItemCallback<RecentChat>() {
    override fun areItemsTheSame(oldItem: RecentChat, newItem: RecentChat): Boolean {
        return oldItem.receiverId == newItem.receiverId
    }

    override fun areContentsTheSame(oldItem: RecentChat, newItem: RecentChat): Boolean {
        return oldItem == newItem
    }

}