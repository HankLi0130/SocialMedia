package tw.iamstar.network

import com.squareup.moshi.Json

data class NotificationRequest(
    @Json(name = "topic") val topic: String,
    @Json(name = "data") val data: Map<String, String>
)
