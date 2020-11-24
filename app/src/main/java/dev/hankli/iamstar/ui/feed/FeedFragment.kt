package dev.hankli.iamstar.ui.feed

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseFragment
import dev.hankli.iamstar.utils.MarginItemDecoration
import dev.hankli.iamstar.utils.UIAction
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

        val options = viewModel.getFirestoreRecyclerOptions(app.influencer)

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
                }
            }
        })

        viewModel.uiAlertEvents.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandle()?.let { messageId ->
                showAlert(messageId)
            }
        })
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
        viewModel.doReaction(objectId, app.user)
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