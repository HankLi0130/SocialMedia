package tw.iamstar.network

import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {

    @POST("/sendFCMToApp")
    suspend fun sendToChannel(@Body notificationRequest: NotificationRequest)
}