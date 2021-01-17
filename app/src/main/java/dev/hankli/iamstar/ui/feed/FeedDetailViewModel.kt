package dev.hankli.iamstar.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ListenerRegistration
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.repo.ProfileRepo
import dev.hankli.iamstar.utils.ArchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedDetailViewModel(
    private val feedRepo: FeedRepo,
    private val profileRepo: ProfileRepo
) : ArchViewModel() {

    val refreshCommentsCode = 1

    private lateinit var feedId: String

    private var _feedData = MutableLiveData<Feed>()
    val feedData: LiveData<Feed>
        get() = _feedData

    private lateinit var feedRegistration: ListenerRegistration

    fun loadFeed(feedId: String, influencerId: String) {
        this.feedId = feedId

        feedRegistration = feedRepo.observeFeed(feedId) { value, error ->
            error?.let {
                showError(R.string.error)
                it.printStackTrace()
                return@observeFeed
            }

            value?.toObject(Feed::class.java)?.let { feed ->
                viewModelScope.launch(IO) {
                    feed.photoURL = profileRepo.getPhotoURL(influencerId)
                    feed.reactionByCurrentUser = feedRepo.getReaction(feedId, currentUserId!!)
                    _feedData.postValue(feed)
                }
            }
        }
    }

    fun getCommentOptions(feedId: String): FirestoreRecyclerOptions<Comment> =
        FirestoreRecyclerOptions.Builder<Comment>()
            .setQuery(feedRepo.queryComments(feedId)) { commentSnapshot ->
                val comment = commentSnapshot.toObject(Comment::class.java)!!
                comment.profile?.get()?.addOnSuccessListener { profileSnapshot ->
                    comment.photoURL = profileSnapshot.getString(Profile.PHOTO_URL)
                    comment.userName = profileSnapshot.getString(Profile.DISPLAY_NAME)
                    notifyView(refreshCommentsCode)
                }
                return@setQuery comment
            }
            .build()


    fun doReaction(feedId: String) {
        viewModelScope.launch(IO) {
            if (feedRepo.hasReaction(feedId, currentUserId!!)) {
                feedRepo.unlike(feedId, currentUserId)
            } else {
                feedRepo.like(feedId, currentUserId)
            }
        }
    }

    fun sendComment(feedId: String, message: String) {
        if (message.isEmpty()) return

        viewModelScope.launch {
            callProgress(true)
            withContext(Dispatchers.IO) { feedRepo.addComment(feedId, currentUserId!!, message) }
            callProgress(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        feedRegistration.remove()
    }
}