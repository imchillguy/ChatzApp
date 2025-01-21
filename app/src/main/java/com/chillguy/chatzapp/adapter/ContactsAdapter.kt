package com.chillguy.chatzapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.diffutil.ContactsDiffUtilCallback
import com.chillguy.chatzapp.listener.IContactsChatListener
import com.chillguy.chatzapp.model.dto.Contacts
import com.chillguy.chatzapp.viewholder.ContactsViewHolder

class ContactsAdapter(private val listener: IContactsChatListener) : RecyclerView.Adapter<ContactsViewHolder>() {

    var contactsList : List<Contacts>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    val differ : AsyncListDiffer<Contacts> = AsyncListDiffer(this, ContactsDiffUtilCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contacts_item, parent, false)
        return ContactsViewHolder(itemView, listener)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bindData(contactsList[position])
    }
}