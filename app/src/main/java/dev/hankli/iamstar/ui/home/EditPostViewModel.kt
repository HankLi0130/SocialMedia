package dev.hankli.iamstar.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.FirebaseUtil.addPost
import dev.hankli.iamstar.utils.FirebaseUtil.auth
import dev.hankli.iamstar.utils.FirebaseUtil.fetchPost
import dev.hankli.iamstar.utils.FirebaseUtil.getPostMediaIds
import dev.hankli.iamstar.utils.FirebaseUtil.updatePostMedia
import dev.hankli.iamstar.utils.FirebaseUtil.uploadPostMedia
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
            val onSuccess = OnSuccessListener<DocumentSnapshot> {
                post = it.toObject(Post::class.java)!!
                setDefaultValues()
            }

            val onFailure = OnFailureListener {
                // TODO Alert message and pop up
            }

            fetchPost(postId, onSuccess, onFailure)
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
        if (!isValid()) {
            // TODO show alert
            return
        }

        if (post.objectId == EMPTY) {
            post.authorId = auth.currentUser!!.uid
            post.influencerId = auth.currentUser!!.uid
            // addPost(post, mediaItems)

            addPost(post, { // success
                val mediaIds = getPostMediaIds(post.objectId, mediaItems.size)
                for ((index, id) in mediaIds.withIndex()) {
                    val mediaItem = mediaItems[index]
                    val name = "$id.${mediaItem.ext}"
                    uploadPostMedia(name, mediaItem.uri!!,
                        { url ->    // success
                            updatePostMedia(
                                post.objectId,
                                Media(
                                    id,
                                    url,
                                    mediaItem.type,
                                    mediaItem.height,
                                    mediaItem.width
                                ),
                                {   // success
                                    _popUp.value = true
                                }, { ex ->
                                    Log.e("test", "fail on updatePostMedia", ex)
                                })

                        }, { ex ->
                            Log.e("test", "fail on uploadPostMedia", ex)
                        })
                }

            }, { ex ->
                Log.e("test", "fail on addPost", ex)
            })
        } else {
            // updatePost(post, listener)
        }
    }

    private fun isValid(): Boolean {
        if (post.content.isEmpty()) return false
        return true
    }
}