package dev.hankli.iamstar.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.BaseViewModel
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.hankli.brookray.event.Event

class FeedViewModel : BaseViewModel() {

    private val feedRepo: FeedRepo by lazy { FeedRepo() }

    private val _refreshFeeds = MutableLiveData<Event<Unit>>()
    val refreshFeeds: LiveData<Event<Unit>>
        get() = _refreshFeeds

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

    fun retrieveReaction(feed: Feed, user: DocumentReference) {
        viewModelScope.launch(Dispatchers.IO) {
            feed.reaction = feedRepo.getReaction(feed.objectId, user)
            withContext(Dispatchers.Main) {
                _refreshFeeds.value = Event(Unit)
            }
        }
    }
}