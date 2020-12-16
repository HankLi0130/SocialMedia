package dev.hankli.iamstar.ui.feed

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.BaseViewModel
import dev.hankli.iamstar.utils.media.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.hankli.brookray.core.constant.EMPTY

class EditFeedViewModel : BaseViewModel() {

    private lateinit var feed: Feed

    private val feedRepo = FeedRepo()

    private val _contentData = MutableLiveData<String>()
    val contentData: LiveData<String>
        get() = _contentData

    private val mediaFiles = mutableListOf<MediaFile>()

    private val _mediaFilesData = MutableLiveData<List<MediaFile>>()
    val mediaFilesData: LiveData<List<MediaFile>>
        get() = _mediaFilesData

    private val _locationData = MutableLiveData<String?>()
    val locationData: LiveData<String?>
        get() = _locationData

    fun loadPost(postId: String) {
        if (postId == EMPTY) {
            feed = Feed()
        } else {
            viewModelScope.launch {
                callProgress(true)
                withContext(IO) { feed = feedRepo.fetchFeed(postId) }
                setDefaultValues()
                callProgress(false)
            }
        }
    }

    private fun setDefaultValues() {
        _locationData.value = feed.location
        _contentData.value = feed.content

        addMediaFiles(feed.medias.map { it.toMediaFile() })
    }

    fun onContentChanged(text: CharSequence?) {
        val content = text?.toString() ?: EMPTY
        feed.content = content
    }

    fun addMediaFiles(list: List<MediaFile>) {
        mediaFiles.addAll(list)
        _mediaFilesData.value = mediaFiles
    }

    fun removeMediaItemAt(position: Int) {
        mediaFiles.removeAt(position)
        _mediaFilesData.value = mediaFiles
    }

    fun setLocation(location: String?, latitude: Double?, longitude: Double?) {
        feed.location = location
        feed.latitude = latitude
        feed.longitude = longitude
        _locationData.value = feed.location
    }

    fun submit(
        contentResolver: ContentResolver,
        currentUser: DocumentReference,
        influencer: DocumentReference
    ) {
        val errorMessageRes = checkValid()
        if (errorMessageRes.isNotEmpty()) {
            showErrors(errorMessageRes)
            return
        }

        // If the post doesn't have an objectId, create a new post
        // Instead of, update the post
        if (feed.objectId == EMPTY) {
            feed.author = currentUser
            feed.influencer = influencer

            viewModelScope.launch(Main) {
                callProgress(true)

                val uploadingMedias = withContext(Default) {
                    val localMediaFiles = mediaFiles.filterIsInstance(LocalMediaFile::class.java)
                    return@withContext toUploadingMedias(contentResolver, localMediaFiles)
                }

                withContext(IO) {
                    feedRepo.addFeed(this, feed, uploadingMedias)
                }

                callProgress(false)
                popBack()
            }
        } else {
            viewModelScope.launch(Main) {
                callProgress(true)

                val uploadingMedias = withContext(Default) {
                    val localMediaFiles = mediaFiles.filterIsInstance(LocalMediaFile::class.java)
                    return@withContext toUploadingMedias(contentResolver, localMediaFiles)
                }

                val removingMediaIds = withContext(Default) {
                    val originIds = feed.medias.map { it.objectId }
                    val keepingIds = mediaFiles.filterIsInstance(RemoteMediaFile::class.java)
                        .map { it.id }
                    return@withContext originIds.subtract(keepingIds)
                }

                withContext(IO) {
                    feedRepo.updateFeed(this, feed, uploadingMedias, removingMediaIds)
                }

                callProgress(false)
                popBack()
            }
//            transfer(mediaFiles.filter { it.objectId == EMPTY })
//                .flatMapCompletable { mediasForUploading ->
//                    val originIds = feed.medias.map { it.objectId }
//                    val updatedIds = mediaFiles.map { it.objectId }
//                    val idsForRemoving = originIds.subtract(updatedIds)
//                    feedRepo.updateFeed(feed, mediasForUploading, idsForRemoving)
//                }
//                .doOnComplete {
//                    callProgress(false)
//                }
//                .subscribe({
//                    popBack()
//                }, { ex ->
//                    Log.e("test", "update post failed", ex)
//                })
//                .addTo(disposables)
        }
    }

    private fun checkValid(): List<Int> {
        val errorMessageRes = mutableListOf<Int>()
        if (feed.content.isEmpty()) errorMessageRes.add(R.string.error_feed_content_is_empty)
        return errorMessageRes
    }

    fun isMediaFilesEmpty() = mediaFiles.isEmpty()

    fun getMediaFilesCount() = mediaFiles.size

    fun getMediaFilesType() = mediaFiles[0].type()
}