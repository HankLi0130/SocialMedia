package app.hankdev.ui.feed

import androidx.lifecycle.viewModelScope
import app.hankdev.data.models.firestore.Feed
import app.hankdev.firebase.AuthManager
import app.hankdev.repo.FeedRepo
import app.hankdev.utils.ArchViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(
    private val feedRepo: FeedRepo,
    private val authManager: AuthManager
) : ArchViewModel() {

    val refreshFeedsCode = 1

    fun getFeedOptions(currentUserId: String) = FirestoreRecyclerOptions.Builder<Feed>()
        .setQuery(feedRepo.queryAllBy(currentUserId), Feed::class.java)
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
            if (feedRepo.hasReaction(feedId, authManager.currentUserId!!)) {
                feedRepo.unlike(feedId, authManager.currentUserId!!)
            } else {
                feedRepo.like(feedId, authManager.currentUserId!!)
            }
        }
    }
}