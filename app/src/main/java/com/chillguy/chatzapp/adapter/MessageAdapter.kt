package com.chillguy.chatzapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.diffutil.ChatMessageDiffUtilCallback
import com.chillguy.chatzapp.enums.ChatType
import com.chillguy.chatzapp.model.dto.ChatMessage
import com.chillguy.chatzapp.viewholder.MessageViewHolder
import com.chillguy.chatzapp.viewholder.ReceiverMessageViewHolder
import com.chillguy.chatzapp.viewholder.SenderMessageViewHolder

class MessageAdapter: RecyclerView.Adapter<MessageViewHolder>() {

    var chatMessageList : List<ChatMessage>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ: AsyncListDiffer<ChatMessage> = AsyncListDiffer(this, ChatMessageDiffUtilCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
       return when (viewType) {
            ChatType.SENDER.ordinal -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sender_item, parent, false)
                SenderMessageViewHolder(itemView)
            }
           ChatType.RECEIVER.ordinal -> {
               val itemView = LayoutInflater.from(parent.context).inflate(R.layout.receiver_item, parent, false)
               ReceiverMessageViewHolder(itemView)
           }
           else -> {
               throw UnsupportedOperationException("Not valid viewType")
           }
        }
    }

    override fun getItemCount(): Int {
        return chatMessageList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindItems(chatMessageList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return chatMessageList[position].type.ordinal
    }


}