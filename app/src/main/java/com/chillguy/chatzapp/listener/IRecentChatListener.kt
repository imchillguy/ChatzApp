package com.chillguy.chatzapp.listener

import com.chillguy.chatzapp.model.dto.RecentChat

interface IRecentChatListener {
    fun onRecentChatClicked(recentChat: RecentChat)
}