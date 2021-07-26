package app.hankdev.data.models.messaging

import app.hankdev.R
import app.hankdev.ui.feed.FeedDetailFragmentArgs

data class FeedData(val feedId: String) : app.hankdev.data.models.messaging.MessagingData {

    override fun getTitleResId() = R.string.feed_reminding

    override fun getDestId() = R.id.feedDetailFragment

    override fun getArgs() = FeedDetailFragmentArgs(feedId).toBundle()
}
