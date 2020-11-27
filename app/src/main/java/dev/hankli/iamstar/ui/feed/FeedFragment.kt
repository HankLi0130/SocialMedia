package dev.hankli.iamstar.ui.feed

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.utils.BaseArchFragment
import dev.hankli.iamstar.utils.MarginItemDecoration
import dev.hankli.iamstar.utils.ext.isInternetConnected
import kotlinx.android.synthetic.main.fragment_feed.*
import tw.hankli.brookray.constant.EMPTY

class FeedFragment : BaseArchFragment<FeedViewModel>(R.layout.fragment_feed) {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.fragment_home

    override val viewModel: FeedViewModel by viewModels()

    private lateinit var feedCardAdapter: FeedCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val options = FirestoreRecyclerOptions.Builder<Feed>()
            .setQuery(FeedManager.queryByInfluencer(app.influencer)) { snapshot ->
                val feed = snapshot.toObject(Feed::class.java) ?: error("Feed parse failed !")
                if (requireContext().isInternetConnected()) {
                    viewModel.retrieveReaction(feed, app.user)
                }
                return@setQuery feed
            }
            .build()

        feedCardAdapter = FeedCardAdapter(options).apply {
            onItemOptionsClick = ::onFeedCardOptionsClick
            onItemReactionClick = ::onFeedCardReactionClick
            onItemCommentClick = ::onFeedCardCommentClick
        }

        view_feeds.apply {
            setHasFixedSize(true)
            adapter = feedCardAdapter
            addItemDecoration(
                MarginItemDecoration(resources.getDimension(R.dimen.distance_12_dp).toInt())
            )
        }

        viewModel.refreshFeeds.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandle()?.let { feedCardAdapter.notifyDataSetChanged() }
        }
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

    private fun onFeedCardOptionsClick(feedId: String) {
        showListDialog(R.string.feed_actions_title, R.array.feed_actions) { which ->
            when (which) {
                0 -> toEditFeedFragment(feedId)
                1 -> viewModel.deleteFeed(feedId)
            }
        }
    }

    private fun onFeedCardReactionClick(feedId: String) {
        if (requireContext().isInternetConnected()) {
            viewModel.doReaction(feedId, app.user)
        } else showNoInternet()
    }

    private fun onFeedCardCommentClick(feedId: String) {
        findNavController().navigate(
            FeedFragmentDirections.actionFeedFragmentToCommentFragment(feedId)
        )
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