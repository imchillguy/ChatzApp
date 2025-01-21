package com.chillguy.chatzapp.listener

import com.chillguy.chatzapp.model.dto.Contacts

interface IContactsChatListener {
    fun onContactsClick(contacts: Contacts)
}