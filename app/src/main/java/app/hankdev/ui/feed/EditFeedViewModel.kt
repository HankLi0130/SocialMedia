package app.hankdev.ui.feed

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.hankdev.R
import app.hankdev.data.models.firestore.Feed
import app.hankdev.firebase.AuthManager
import app.hankdev.repo.FeedRepo
import app.hankdev.repo.ProfileRepo
import app.hankdev.utils.ArchViewModel
import app.hankdev.utils.media.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.hankli.brookray.core.constant.EMPTY

class EditFeedViewModel(
    private val feedRepo: FeedRepo,
    private val profileRepo: ProfileRepo,
    private val authManager: AuthManager
) :
    ArchViewModel() {

    private lateinit var feed: Feed

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

    fun loadFeed(feedId: String) {
        if (feedId == EMPTY) {
            feed = Feed()
        } else {
            viewModelScope.launch {
                callProgress(true)
                withContext(IO) { feed = feedRepo.getFeed(feedId)!! }
                setDefaultValues()
                callProgress(false)
            }
        }
    }

    suspend fun getPhotoURL(userId: String): String? = profileRepo.getPhotoURL(userId)

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

    fun submit(contentResolver: ContentResolver) {
        val errorMessageRes = checkValid()
        if (errorMessageRes.isNotEmpty()) {
            showErrors(errorMessageRes)
            return
        }

        // If the post doesn't have an objectId, create a new post
        // Instead of, update the post
        if (feed.id == EMPTY) {
            viewModelScope.launch(Main) {
                callProgress(true)

                val uploadingMedias = withContext(Default) {
                    val localMediaFiles = mediaFiles.filterIsInstance(LocalMediaFile::class.java)
                    return@withContext toUploadingMedias(contentResolver, localMediaFiles)
                }

                withContext(IO) {
                    feedRepo.addFeed(this, feed, authManager.currentUserId!!, uploadingMedias)
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
                    val originIds = feed.medias.map { it.id }
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
        }
    }

    private fun checkValid(): List<Int> {
        val errorMessageRes = mutableListOf<Int>()
        if (feed.content.isEmpty()) errorMessageRes.add(R.string.error_feed_content_is_empty)
        return errorMessageRes
    }

    fun isMediaFilesEmpty() = mediaFiles.isEmpty()

    fun getMediaFilesCount() = mediaFiles.size

    fun getMediaFilesType() = mediaFiles[0].type
}