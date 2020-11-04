package dev.hankli.iamstar.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.BaseViewModel
import dev.hankli.iamstar.utils.FirebaseUtil.addPost
import dev.hankli.iamstar.utils.FirebaseUtil.auth
import dev.hankli.iamstar.utils.FirebaseUtil.fetchPost
import dev.hankli.iamstar.utils.FirebaseUtil.updatePost
import dev.hankli.iamstar.utils.media.MediaForBrowsing
import dev.hankli.iamstar.utils.media.MediaForUploading
import dev.hankli.iamstar.utils.media.toForBrowsing
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import tw.hankli.brookray.constant.EMPTY

class EditPostViewModel : BaseViewModel() {

    private lateinit var post: Post

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
            post = Post()
        } else {
            showProgress()
            fetchPost(postId)
                .doAfterSuccess { dismissProgress() }
                .subscribe(
                    { post ->
                        this.post = post
                        setDefaultValues()
                    },
                    { ex -> }
                ).addTo(disposables)
        }
    }

    private fun setDefaultValues() {
        _locationData.value = post.location
        _contentData.value = post.content

        addToMediaItems(post.medias.map { it.toForBrowsing() })
    }

    fun onContentChanged(text: CharSequence?) {
        val content = text?.toString() ?: EMPTY
        post.content = content
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
        post.location = location
        post.latitude = latitude
        post.longitude = longitude
        _locationData.value = post.location
    }

    fun submit(transfer: (List<MediaForBrowsing>) -> Single<List<MediaForUploading>>) {
        if (!isValid()) {
            showAlert(R.string.alert_post_is_invalid)
            return
        }

        showProgress()

        // If the post doesn't have objectId, create a new one
        // Instead of, update the post
        if (post.objectId == EMPTY) {
            post.authorId = auth.currentUser!!.uid
            post.influencerId = auth.currentUser!!.uid

            transfer(mediaItems)
                .flatMap {
                    addPost(post, it)
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
                    val originIds = post.medias.map { it.objectId }
                    val updatedIds = mediaItems.map { it.objectId }
                    val idsForRemoving = originIds.subtract(updatedIds)
                    updatePost(post, mediasForUploading, idsForRemoving)
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
        if (post.content.isEmpty()) return false
        return true
    }

    fun isMediaItemsEmpty() = mediaItems.isEmpty()

    fun getMediaItemCount(): Int = mediaItems.size

    fun getMediaItemsType(): String? {
        val media = mediaItems.getOrNull(0)
        return media?.type
    }
}