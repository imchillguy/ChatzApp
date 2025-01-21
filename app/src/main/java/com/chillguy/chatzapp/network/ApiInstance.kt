package com.chillguy.chatzapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiInstance {

    companion object {

        private val okHttpClient : OkHttpClient by lazy {
            OkHttpClient.Builder()
                .build()
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        fun <T> getApiService(service: Class<T>): T{
            return retrofit.create(service)
        }

    }

}