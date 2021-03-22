package tw.iamstar.network

data class NotificationRequest(
    val topic: String,
    val title: String,
    val content: String
)
