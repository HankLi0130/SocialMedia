package dev.hankli.iamstar.ui.feed

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.R
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.BaseViewModel
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedViewModel : BaseViewModel() {

    private val feedRepo: FeedRepo by lazy { FeedRepo() }

    fun deleteFeed(objectId: String) {
        showProgress()
        feedRepo.deleteFeed(objectId)
            .doOnComplete { dismissProgress() }
            .subscribe({

            }, {
                showAlert(R.string.alert_delete_feed_failed)
            })
            .addTo(disposables)
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

    fun getFirestoreRecyclerOptions(influencer: DocumentReference) =
        feedRepo.getFirestoreRecyclerOptions(influencer)
}