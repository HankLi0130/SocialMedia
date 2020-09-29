package dev.hankli.iamstar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.FirebaseUtil.setPost
import dev.hankli.iamstar.utils.MediaItem
import tw.hankli.brookray.constant.EMPTY

class EditPostViewModel : ViewModel() {

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
            setDefaultValues()
        } else {
            // TODO loading from Firestore
        }
    }

    private fun setDefaultValues() {
        _locationData.postValue(post.location)
        _contentData.postValue(post.content)
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
        setPost(post, OnCompleteListener {
            _popUp.postValue(it.isSuccessful)
        })
    }
}