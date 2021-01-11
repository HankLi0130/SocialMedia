package dev.hankli.iamstar.ui.feed

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.firebase.AuthManager
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.ext.isInternetConnected
import kotlinx.android.synthetic.main.fragment_feed.*
import tw.hankli.brookray.core.constant.EMPTY
import tw.hankli.brookray.recyclerview.decoration.MarginItemDecoration

class FeedFragment : ArchFragment<FeedViewModel>(R.layout.fragment_feed, R.menu.fragment_home) {

    override val viewModel: FeedViewModel by viewModels()

    private lateinit var feedCardAdapter: FeedCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Role Permission
        val writable = AuthManager.currentUserId == app.influencerId
        setMenuVisibility(writable)

        val options = FirestoreRecyclerOptions.Builder<Feed>()
            .setQuery(FeedManager.queryByInfluencer(app.influencerId)) { snapshot ->
                val feed = snapshot.toObject(Feed::class.java)!!
                if (requireContext().isInternetConnected()) {
                    viewModel.retrieveReaction(feed)
                }
                return@setQuery feed
            }
            .build()

        feedCardAdapter = FeedCardAdapter(writable, options).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            onItemClick = ::onFeedCardClick
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
                0 -> toEditFeedFragment(feedId)
                1 -> viewModel.deleteFeed(feedId)
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

    override fun onStart() {
        super.onStart()
        feedCardAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        feedCardAdapter.stopListening()
    }
}