package com.chillguy.chatzapp.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.chillguy.chatzapp.model.dto.Contacts

class ContactsDiffUtilCallback: DiffUtil.ItemCallback<Contacts>() {
    override fun areItemsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: Contacts, newItem: Contacts): Boolean {
        return oldItem == newItem
    }
}