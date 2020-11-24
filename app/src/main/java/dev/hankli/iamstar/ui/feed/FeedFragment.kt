package dev.hankli.iamstar.ui.feed

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.utils.BaseFragment
import dev.hankli.iamstar.utils.MarginItemDecoration
import dev.hankli.iamstar.utils.UIAction
import dev.hankli.iamstar.utils.ext.isInternetConnected
import kotlinx.android.synthetic.main.fragment_feed.*
import tw.hankli.brookray.constant.EMPTY

class FeedFragment : BaseFragment(R.layout.fragment_feed) {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.fragment_home

    private val viewModel by viewModels<FeedViewModel>()

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
        }

        view_feeds.apply {
            setHasFixedSize(true)
            adapter = feedCardAdapter
            addItemDecoration(
                MarginItemDecoration(resources.getDimension(R.dimen.distance_12_dp).toInt())
            )
        }

        viewModel.uiEvents.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandle()?.let { action ->
                when (action) {
                    UIAction.SHOW_PROGRESS -> showProgressDialog()
                    UIAction.DISMISS_PROGRESS -> dismissProgressDialog()
                    UIAction.POP_BACK -> popBack()
                    UIAction.REFRESH -> refresh()
                }
            }
        })

        viewModel.uiAlertEvents.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandle()?.let { messageId ->
                showAlert(messageId)
            }
        })
    }

    private fun refresh() {
        feedCardAdapter.notifyDataSetChanged()
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

    private fun onFeedCardOptionsClick(objectId: String) {
        showListDialog(R.string.feed_actions_title, R.array.feed_actions) { which ->
            when (which) {
                0 -> toEditFeedFragment(objectId)
                1 -> viewModel.deleteFeed(objectId)
            }
        }
    }

    private fun onFeedCardReactionClick(objectId: String) {
        if (requireContext().isInternetConnected()) {
            viewModel.doReaction(objectId, app.user)
        } else showNoInternet()
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