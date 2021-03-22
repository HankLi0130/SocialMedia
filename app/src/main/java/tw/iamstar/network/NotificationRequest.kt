package tw.iamstar.network

import com.squareup.moshi.Json

data class NotificationRequest(
    @Json(name = "topic") val topic: String,
    @Json(name = "title") val title: String,
    @Json(name = "content") val content: String
)
