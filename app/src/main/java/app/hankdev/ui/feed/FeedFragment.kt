package app.hankdev.ui.feed

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import app.hankdev.R
import app.hankdev.data.enums.AuthenticationState
import app.hankdev.ui.SharedViewModel
import app.hankdev.utils.ArchFragment
import app.hankdev.utils.ext.isInternetConnected
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tw.hankli.brookray.core.constant.EMPTY
import tw.hankli.brookray.core.log.logError
import tw.hankli.brookray.recyclerview.decoration.MarginItemDecoration

class FeedFragment : ArchFragment<FeedViewModel>(R.layout.fragment_feed, R.menu.fragment_home) {

    override val viewModel: FeedViewModel by viewModel()

    private val sharedViewModel: SharedViewModel by sharedViewModel()

    private var feedCardAdapter: FeedCardAdapter? = null

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        sharedViewModel.authenticationState.observe(viewLifecycleOwner) { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> setUI()
                AuthenticationState.UNAUTHENTICATED -> navController.navigate(
                    FeedFragmentDirections.actionGlobalAuthFragment()
                )
                else -> {
                    logError("New $authenticationState state that doesn't require any UI change")
                }
            }
        }
    }

    private fun setUI() {
        feedCardAdapter = FeedCardAdapter(viewModel.getFeedOptions()).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            onItemClick = ::onFeedCardClick
            onItemOptionsClick = ::onFeedCardOptionsClick
            onItemReactionClick = ::onFeedCardReactionClick
            onItemCommentClick = ::onFeedCardCommentClick
            startListening()
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
        if (code == viewModel.refreshFeedsCode) feedCardAdapter?.notifyDataSetChanged()
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
                // delete
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

    override fun onStop() {
        super.onStop()
        feedCardAdapter?.stopListening()
    }
}