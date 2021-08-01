package app.hankdev.ui.feed

import androidx.lifecycle.viewModelScope
import app.hankdev.data.models.firestore.Feed
import app.hankdev.repo.FeedRepo
import app.hankdev.utils.ArchViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(private val feedRepo: FeedRepo) : ArchViewModel() {

    val refreshFeedsCode = 1

    fun getFeedOptions() = FirestoreRecyclerOptions.Builder<Feed>()
        .setQuery(feedRepo.query(10)) { snapshot ->
            val feed = snapshot.toObject(Feed::class.java)!!

            viewModelScope.launch(Main) {
                withContext(IO) {
                    feed.reactionByCurrentUser =
                        feedRepo.getReaction(feed.objectId, currentUserId!!)
                }
                notifyView(refreshFeedsCode)
            }

            return@setQuery feed
        }
        .build()

    fun deleteFeed(feedId: String) {
        viewModelScope.launch(Main) {
            callProgress(true)
            withContext(IO) {
                feedRepo.removeFeed(feedId)
            }
            callProgress(false)
        }
    }

    fun doReaction(feedId: String) {
        viewModelScope.launch(IO) {
            if (feedRepo.hasReaction(feedId, currentUserId!!)) {
                feedRepo.unlike(feedId, currentUserId)
            } else {
                feedRepo.like(feedId, currentUserId)
            }
        }
    }
}