package com.chillguy.chatzapp

import android.app.Application
import android.content.Context
import com.chillguy.chatzapp.store.DataStoreManager

class ChatzApplication: Application() {

    lateinit var dataStoreManager: DataStoreManager

    companion object {
        lateinit var mApplicationContext: Context
    }

    override fun onCreate() {
        ChatzApplication.mApplicationContext = applicationContext
        dataStoreManager = DataStoreManager(applicationContext)
        super.onCreate()
    }

}