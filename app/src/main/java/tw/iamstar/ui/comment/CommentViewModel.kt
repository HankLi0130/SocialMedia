package tw.iamstar.ui.comment

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.iamstar.repo.FeedRepo
import tw.iamstar.utils.ArchViewModel

class CommentViewModel(private val feedRepo: FeedRepo) : ArchViewModel() {

    fun queryComments(feedId: String): Query {
        return feedRepo.queryComments(feedId)
    }

    fun sendComment(feedId: String, message: String) {
        if (message.isEmpty()) return

        viewModelScope.launch {
            callProgress(true)
            withContext(Dispatchers.IO) { feedRepo.addComment(feedId, message, currentUserId!!) }
            callProgress(false)
        }
    }
}