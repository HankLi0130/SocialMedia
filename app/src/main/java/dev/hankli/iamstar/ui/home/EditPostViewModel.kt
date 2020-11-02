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
import dev.hankli.iamstar.utils.media.MediaForBrowse
import dev.hankli.iamstar.utils.media.MediaForUpload
import dev.hankli.iamstar.utils.media.toForBrowse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import tw.hankli.brookray.constant.EMPTY

class EditPostViewModel : BaseViewModel() {

    private lateinit var post: Post

    private val _contentData = MutableLiveData<String>()
    val contentData: LiveData<String>
        get() = _contentData

    private val mediaItems = mutableListOf<MediaForBrowse>()

    private val _mediaItemsData = MutableLiveData<List<MediaForBrowse>>()
    val mediaItemsData: LiveData<List<MediaForBrowse>>
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

        addToMediaItems(post.medias.map { it.toForBrowse() })
    }

    fun onContentChanged(text: CharSequence?) {
        val content = text?.toString() ?: EMPTY
        post.content = content
    }

    fun addToMediaItems(list: List<MediaForBrowse>) {
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

    fun submit(transfer: (List<MediaForBrowse>) -> Single<List<MediaForUpload>>) {
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
            updatePost(post, mediaItems)
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