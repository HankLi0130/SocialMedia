package dev.hankli.iamstar.ui.feed

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel : BaseViewModel() {

    val refreshFeedsCode = 1

    private val feedRepo: FeedRepo by lazy { FeedRepo() }

    fun deleteFeed(feedId: String) {
        viewModelScope.launch(Main) {
            callProgress(true)
            withContext(IO) {
                feedRepo.removeFeed(feedId)
            }
            callProgress(false)
        }

//        feedRepo.deleteFeed(objectId)
//            .doOnComplete { callProgress(false) }
//            .subscribe({
//
//            }, {
//                showAlert(R.string.alert_delete_feed_failed)
//            })
//            .addTo(disposables)
    }

    fun doReaction(feedId: String, user: DocumentReference) {
        viewModelScope.launch(Dispatchers.IO) {
            if (feedRepo.hasReaction(feedId, user)) {
                feedRepo.unlike(feedId, user)
            } else {
                feedRepo.like(feedId, user)
            }
        }
    }

    fun retrieveReaction(feed: Feed, user: DocumentReference) {
        viewModelScope.launch(Main) {
            withContext(IO) { feed.reaction = feedRepo.getReaction(feed.objectId, user) }
            notifyView(refreshFeedsCode)
        }
    }
}