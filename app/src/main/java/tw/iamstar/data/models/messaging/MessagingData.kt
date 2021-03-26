package tw.iamstar.data.models.messaging

sealed class MessagingData

data class FeedData(val feedId: String) : MessagingData() {
    companion object {
        const val KEY = "feedKey"
    }
}
