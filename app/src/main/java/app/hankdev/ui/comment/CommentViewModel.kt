package app.hankdev.ui.comment

import androidx.lifecycle.viewModelScope
import app.hankdev.firebase.AuthManager
import app.hankdev.repo.FeedRepo
import app.hankdev.utils.ArchViewModel
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentViewModel(
    private val feedRepo: FeedRepo,
    private val authManager: AuthManager
) : ArchViewModel() {

    fun queryComments(feedId: String): Query {
        return feedRepo.queryComments(feedId)
    }

    fun sendComment(feedId: String, message: String) {
        if (message.isEmpty()) return

        viewModelScope.launch {
            callProgress(true)
            withContext(Dispatchers.IO) {
                feedRepo.addComment(
                    feedId,
                    message,
                    authManager.currentUserId!!
                )
            }
            callProgress(false)
        }
    }
}