package dev.hankli.iamstar.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.BaseViewModel
import dev.hankli.iamstar.utils.media.MediaForBrowsing
import dev.hankli.iamstar.utils.media.MediaForUploading
import dev.hankli.iamstar.utils.media.toForBrowsing
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.hankli.brookray.constant.EMPTY

class EditFeedViewModel : BaseViewModel() {

    private lateinit var feed: Feed

    private val feedRepo = FeedRepo()

    private val _contentData = MutableLiveData<String>()
    val contentData: LiveData<String>
        get() = _contentData

    private val mediaItems = mutableListOf<MediaForBrowsing>()

    private val _mediaItemsData = MutableLiveData<List<MediaForBrowsing>>()
    val mediaItemsData: LiveData<List<MediaForBrowsing>>
        get() = _mediaItemsData

    private val _locationData = MutableLiveData<String?>()
    val locationData: LiveData<String?>
        get() = _locationData

    fun loadPost(postId: String) {
        if (postId == EMPTY) {
            feed = Feed()
        } else {
            viewModelScope.launch {
                showProgress()
                withContext(IO) { feed = feedRepo.fetchFeed(postId) }
                setDefaultValues()
                dismissProgress()
            }
        }
    }

    private fun setDefaultValues() {
        _locationData.value = feed.location
        _contentData.value = feed.content

        addToMediaItems(feed.medias.map { it.toForBrowsing() })
    }

    fun onContentChanged(text: CharSequence?) {
        val content = text?.toString() ?: EMPTY
        feed.content = content
    }

    fun addToMediaItems(list: List<MediaForBrowsing>) {
        mediaItems.addAll(list)
        _mediaItemsData.value = mediaItems
    }

    fun removeMediaItemAt(position: Int) {
        mediaItems.removeAt(position)
        _mediaItemsData.value = mediaItems
    }

    fun setLocation(location: String?, latitude: Double?, longitude: Double?) {
        feed.location = location
        feed.latitude = latitude
        feed.longitude = longitude
        _locationData.value = feed.location
    }

    fun submit(
        currentUser: DocumentReference,
        influencer: DocumentReference,
        transfer: (List<MediaForBrowsing>) -> Single<List<MediaForUploading>>
    ) {
        if (!isValid()) {
            showAlert(R.string.alert_post_is_invalid)
            return
        }

        showProgress()

        // If the post doesn't have objectId, create a new one
        // Instead of, update the post
        if (feed.objectId == EMPTY) {
            feed.author = currentUser
            feed.influencer = influencer

            transfer(mediaItems)
                .flatMap {
                    feedRepo.addFeed(feed, it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    dismissProgress()
                }
                .subscribe({
                    popBack()
                }, { ex ->
                    Log.e("test", "add post failed", ex)
                })
                .addTo(disposables)

        } else {
            transfer(mediaItems.filter { it.objectId == EMPTY })
                .flatMapCompletable { mediasForUploading ->
                    val originIds = feed.medias.map { it.objectId }
                    val updatedIds = mediaItems.map { it.objectId }
                    val idsForRemoving = originIds.subtract(updatedIds)
                    feedRepo.updateFeed(feed, mediasForUploading, idsForRemoving)
                }
                .doOnComplete {
                    dismissProgress()
                }
                .subscribe({
                    popBack()
                }, { ex ->
                    Log.e("test", "update post failed", ex)
                })
                .addTo(disposables)
        }
    }

    private fun isValid(): Boolean {
        if (feed.content.isEmpty()) return false
        return true
    }

    fun isMediaItemsEmpty() = mediaItems.isEmpty()

    fun getMediaItemCount(): Int = mediaItems.size

    fun getMediaItemsType(): String? {
        val media = mediaItems.getOrNull(0)
        return media?.type
    }
}