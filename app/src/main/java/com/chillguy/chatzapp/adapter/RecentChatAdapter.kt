package com.chillguy.chatzapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.diffutil.RecentChatDiffUtilCallback
import com.chillguy.chatzapp.listener.IRecentChatListener
import com.chillguy.chatzapp.model.dto.RecentChat
import com.chillguy.chatzapp.viewholder.RecentChatViewHolder

class RecentChatAdapter(private val listener: IRecentChatListener): RecyclerView.Adapter<RecentChatViewHolder>() {

    var recentChatList: List<RecentChat>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ : AsyncListDiffer<RecentChat> = AsyncListDiffer(this, RecentChatDiffUtilCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recent_chat_item, parent, false)
        return RecentChatViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return recentChatList.size
    }

    override fun onBindViewHolder(holder: RecentChatViewHolder, position: Int) {
        holder.bindData(recentChatList[position], listener)
    }
}