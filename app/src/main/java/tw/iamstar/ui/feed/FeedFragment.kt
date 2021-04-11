package tw.iamstar.ui.feed

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import tw.hankli.brookray.core.constant.EMPTY
import tw.hankli.brookray.recyclerview.decoration.MarginItemDecoration
import tw.iamstar.R
import tw.iamstar.firebase.AuthManager
import tw.iamstar.utils.ArchFragment
import tw.iamstar.utils.ext.isInternetConnected

class FeedFragment : ArchFragment<FeedViewModel>(R.layout.fragment_feed, R.menu.fragment_home) {

    override val viewModel: FeedViewModel by viewModel()

    private lateinit var feedCardAdapter: FeedCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Role Permission
        val writable = AuthManager.currentUserId == app.influencerId
        setMenuVisibility(writable)

        feedCardAdapter = FeedCardAdapter(
            writable,
            viewModel.getFeedOptions(app.influencerId)
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            onItemClick = ::onFeedCardClick
            onItemOptionsClick = ::onFeedCardOptionsClick
            onItemReactionClick = ::onFeedCardReactionClick
            onItemCommentClick = ::onFeedCardCommentClick
            onItemUnpinClick = ::onFeedUnpin
        }

        view_feeds.apply {
            setHasFixedSize(true)
            adapter = feedCardAdapter
            addItemDecoration(
                MarginItemDecoration(resources.getDimension(R.dimen.distance_12_dp).toInt())
            )
        }
    }

    override fun notifyFromViewModel(code: Int) {
        if (code == viewModel.refreshFeedsCode) feedCardAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_feed -> {
                toEditFeedFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toEditFeedFragment(objectId: String = EMPTY) {
        findNavController().navigate(
            FeedFragmentDirections.actionFeedFragmentToEditFeedFragment(objectId)
        )
    }

    private fun onFeedCardClick(feedId: String) {
        findNavController().navigate(
            FeedFragmentDirections.actionFeedFragmentToFeedDetailFragment(feedId)
        )
    }

    private fun onFeedCardOptionsClick(feedId: String) {
        showListDialog(R.string.feed_actions_title, R.array.feed_actions) { which ->
            when (which) {
                // update
                0 -> toEditFeedFragment(feedId)
                // notify
                1 -> viewModel.pushNotification(feedId)
                // pin
                2 -> viewModel.pinFeed(feedId)
                // delete
                3 -> viewModel.deleteFeed(feedId)
            }
        }
    }

    private fun onFeedCardReactionClick(feedId: String) {
        if (requireContext().isInternetConnected()) {
            viewModel.doReaction(feedId)
        } else viewModel.showNoInternet()
    }

    private fun onFeedCardCommentClick(feedId: String) {
        // TODO scroll to comment recyclerview after click this button
        findNavController().navigate(
            FeedFragmentDirections.actionFeedFragmentToFeedDetailFragment(feedId)
        )
    }

    private fun onFeedUnpin(feedId: String) {
        viewModel.unpinFeed(feedId)
    }

    override fun onStart() {
        super.onStart()
        feedCardAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        feedCardAdapter.stopListening()
    }
}