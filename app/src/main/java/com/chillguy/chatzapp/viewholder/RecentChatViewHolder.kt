package com.chillguy.chatzapp.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.listener.IRecentChatListener
import com.chillguy.chatzapp.model.dto.RecentChat
import com.chillguy.chatzapp.utils.Utils
import com.google.android.material.imageview.ShapeableImageView

class RecentChatViewHolder(private val mItemView: View): RecyclerView.ViewHolder(mItemView) {

    private lateinit var userName: TextView
    private lateinit var userMessage: TextView
    private lateinit var userTimeStamp: TextView
    private lateinit var userImage: ShapeableImageView

    init {
        with(mItemView) {
            userName = findViewById(R.id.user_name)
            userMessage = findViewById(R.id.user_message)
            userTimeStamp = findViewById(R.id.time_stamp)
            userImage = findViewById(R.id.user_image)
        }
    }

    fun bindData(recentChat: RecentChat, listener: IRecentChatListener) {
        with (recentChat) {
            userName.text = friendName
            userMessage.text = message
            userTimeStamp.text = Utils.getTimeWithoutSeconds(timeStamp)
            Glide.with(mItemView.context)
                .load(friendImageUrl)
                .centerCrop()
                .into(userImage)
        }

        mItemView.rootView.setOnClickListener {
            listener.onRecentChatClicked(recentChat)
        }
    }

}