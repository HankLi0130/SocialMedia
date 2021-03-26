package tw.iamstar.data.models.messaging

import tw.iamstar.R
import tw.iamstar.ui.feed.FeedDetailFragmentArgs

data class FeedData(val feedId: String) : MessagingData {

    override fun getTitleResId() = R.string.feed_reminding

    override fun getDestId() = R.id.feedDetailFragment

    override fun getArgs() = FeedDetailFragmentArgs(feedId).toBundle()
}
