package tw.iamstar.ui.feed

import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.iamstar.data.models.Feed
import tw.iamstar.repo.FeedRepo
import tw.iamstar.repo.ProfileRepo
import tw.iamstar.utils.ArchViewModel

class FeedViewModel(
    private val feedRepo: FeedRepo,
    private val profileRepo: ProfileRepo
) : ArchViewModel() {

    val refreshFeedsCode = 1

    fun getFeedOptions(influencerId: String) = FirestoreRecyclerOptions.Builder<Feed>()
        .setQuery(feedRepo.queryByInfluencer(influencerId)) { snapshot ->
            val feed = snapshot.toObject(Feed::class.java)!!

            viewModelScope.launch(Main) {
                withContext(IO) {
                    feed.photoURL = profileRepo.getPhotoURL(influencerId)
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