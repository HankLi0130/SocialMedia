package app.hankdev.ui.timeline

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.RecyclerView
import app.hankdev.R
import app.hankdev.ui.feed.FeedCardAdapter
import app.hankdev.utils.ArchFragment
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import tw.hankli.brookray.recyclerview.decoration.MarginItemDecoration

class TimelineFragment :
    ArchFragment<TimelineViewModel>(R.layout.fragment_feed, R.menu.fragment_timeline) {
    override val viewModel: TimelineViewModel by viewModel()

    private lateinit var feedCardAdapter: FeedCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedCardAdapter = FeedCardAdapter(viewModel.getFeedOptions()).apply {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(this)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun onFeedCardClick(feedId: String) {
    }

    private fun onFeedCardOptionsClick(feedId: String) {
    }

    private fun onFeedCardReactionClick(feedId: String) {
    }

    private fun onFeedCardCommentClick(feedId: String) {
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