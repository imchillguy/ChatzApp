package com.chillguy.chatzapp.viewholder

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.enums.ContactStatus
import com.chillguy.chatzapp.extension.ViewExtension.hide
import com.chillguy.chatzapp.listener.IContactsChatListener
import com.chillguy.chatzapp.model.dto.Contacts
import com.google.android.material.imageview.ShapeableImageView

class ContactsViewHolder(private val mItemView: View, private val listener: IContactsChatListener): RecyclerView.ViewHolder(mItemView) {

    private lateinit var contactName: TextView
    private lateinit var contactImage: ShapeableImageView
    private lateinit var contactsStatus: ImageView
    private lateinit var mContext: Context

    init {
        mContext = mItemView.context
        with(mItemView) {
            contactName = findViewById(R.id.contactsName)
            contactImage = findViewById(R.id.contactsImage)
            contactsStatus = findViewById(R.id.contactsStatus)
        }
    }

    fun bindData(contacts: Contacts) {
        mItemView.rootView.setOnClickListener {
            listener.onContactsClick(contacts)
        }

        contactName.text = contacts.name
        Glide.with(mContext)
            .load(contacts.imageUrl)
            .centerCrop()
            .into(contactImage)

        when(contacts.status) {
            ContactStatus.ONLINE -> {
                Glide.with(mContext).load(R.drawable.online_icon).into(contactsStatus)
            }
            ContactStatus.OFFLINE -> {
                Glide.with(mContext).load(R.drawable.offline_icon).into(contactsStatus)
            }
            ContactStatus.DEFAULT -> {
                contactsStatus.hide()
            }
        }
    }


}