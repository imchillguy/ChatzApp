package com.chillguy.chatzapp.network.apiservice

import com.chillguy.chatzapp.model.dto.NotificationMessage
import com.chillguy.chatzapp.network.ApiConstants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers(
        "Authorization: Bearer ${ApiConstants.ACCESS_TOKEN}",
        "Content-Type: application/json"
    )
    @POST(value = "${ApiConstants.PROJECT_ID}/messages:send")
    suspend fun pushNotification(
        @Body notification: NotificationMessage
    ): Response<ResponseBody>
}