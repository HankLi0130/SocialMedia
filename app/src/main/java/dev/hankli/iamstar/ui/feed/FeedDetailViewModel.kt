package dev.hankli.iamstar.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.ArchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.hankli.brookray.core.constant.EMPTY

class FeedDetailViewModel : ArchViewModel() {

    private val feedRepo = FeedRepo()

    fun loadFeed(feedId: String): LiveData<Feed> {
        val liveData = MutableLiveData<Feed>()
        viewModelScope.launch(Main) {
            callProgress(true)
            withContext(IO) {
                val feed = feedRepo.fetchFeed(feedId)
                liveData.postValue(feed)
            }
            callProgress(false)
        }
        return liveData
    }

    fun getCommentOptions(feedId: String): FirestoreRecyclerOptions<Comment> {
        return FirestoreRecyclerOptions.Builder<Comment>()
            .setQuery(FeedManager.queryComments(feedId)) { commentSnapshot ->
                val comment = commentSnapshot.toObject(Comment::class.java)!!
                Log.i("test", "${comment.objectId} query comments")
                comment.profile?.let { doc ->
                    doc.get().addOnSuccessListener { profileSnapshot ->
                        Log.i("test", "${comment.objectId} query profile")
                        comment.commenterPhotoURL =
                            profileSnapshot.getString(Profile.PHOTO_URL) ?: EMPTY
                        comment.commenterName =
                            profileSnapshot.getString(Profile.DISPLAY_NAME) ?: EMPTY
                    }
                }
                return@setQuery comment
            }
            .build()
    }

    fun doReaction(feedId: String) {
        viewModelScope.launch(IO) {
            if (feedRepo.hasReaction(feedId)) {
                feedRepo.unlike(feedId)
            } else {
                feedRepo.like(feedId)
            }
        }
    }

    fun sendComment(feedId: String, message: String) {
        if (message.isEmpty()) return

        viewModelScope.launch {
            callProgress(true)
            withContext(Dispatchers.IO) { feedRepo.addComment(feedId, message) }
            callProgress(false)
        }
    }
}