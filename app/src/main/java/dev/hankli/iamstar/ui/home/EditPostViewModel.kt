package dev.hankli.iamstar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.MediaItem
import tw.hankli.brookray.constant.EMPTY

class EditPostViewModel : ViewModel() {

    private lateinit var post: Post

    private val mediaItems = mutableListOf<MediaItem>()

    private val _mediaItemsData = MutableLiveData<List<MediaItem>>()
    val mediaItemsData: LiveData<List<MediaItem>>
        get() = _mediaItemsData

    fun loadPost(postId: String, onCompleted: (Post) -> Unit) {
        if (postId == EMPTY) {
            post = Post()
            onCompleted(post)
        } else {
            // TODO loading from Firestore
        }
    }

    fun addToMediaItems(list: List<MediaItem>) {
        mediaItems.addAll(list)
        _mediaItemsData.value = mediaItems
    }

    fun removeMediaItemAt(position: Int) {
        mediaItems.removeAt(position)
        _mediaItemsData.value = mediaItems
    }
}