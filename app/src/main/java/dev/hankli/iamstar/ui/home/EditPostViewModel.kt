package dev.hankli.iamstar.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.BaseViewModel
import dev.hankli.iamstar.utils.FirebaseUtil.addPost
import dev.hankli.iamstar.utils.FirebaseUtil.auth
import dev.hankli.iamstar.utils.FirebaseUtil.fetchPostAndMedias
import dev.hankli.iamstar.utils.FirebaseUtil.updatePost
import dev.hankli.iamstar.utils.MediaItem
import dev.hankli.iamstar.utils.toMediaItem
import io.reactivex.rxkotlin.addTo
import tw.hankli.brookray.constant.EMPTY

class EditPostViewModel : BaseViewModel() {

    private lateinit var post: Post

    private val _contentData = MutableLiveData<String>()
    val contentData: LiveData<String>
        get() = _contentData

    private val mediaItems = mutableListOf<MediaItem>()

    private val _mediaItemsData = MutableLiveData<List<MediaItem>>()
    val mediaItemsData: LiveData<List<MediaItem>>
        get() = _mediaItemsData

    private val _locationData = MutableLiveData<String?>()
    val locationData: LiveData<String?>
        get() = _locationData

    private val _popUp = MutableLiveData<Boolean>()
    val popUp: LiveData<Boolean>
        get() = _popUp

    fun loadPost(postId: String) {
        if (postId == EMPTY) {
            post = Post()
        } else {
            fetchPostAndMedias(postId).subscribe(
                { post ->
                    this.post = post
                    setDefaultValues()
                },
                { ex -> }
            ).addTo(disposables)
        }
    }

    private fun setDefaultValues() {
        _locationData.postValue(post.location)
        _contentData.postValue(post.content)

        addToMediaItems(post.medias.map { it.toMediaItem() })
    }

    fun onContentChanged(text: CharSequence?) {
        val content = text?.toString() ?: EMPTY
        post.content = content
    }

    fun addToMediaItems(list: List<MediaItem>) {
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

    fun submit() {
        if (!isValid()) {
            // TODO show alert
            return
        }

        if (post.objectId == EMPTY) {
            post.authorId = auth.currentUser!!.uid
            post.influencerId = auth.currentUser!!.uid

            addPost(post, mediaItems)
                .doOnComplete { }
                .subscribe({
                    Log.i("test", "add post successful")
                }, { ex ->
                    Log.e("test", "add post failed", ex)
                })
                .addTo(disposables)

        } else {
            updatePost(post,
                // Update post successful in Firestore
                {

                },
                // Update post failed in Firestore
                { ex ->
                    Log.e("test", "update post failed", ex)
                })
        }
    }

    private fun isValid(): Boolean {
        if (post.content.isEmpty()) return false
        return true
    }
}