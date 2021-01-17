package dev.hankli.iamstar.ui.comment

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.firebase.AuthManager
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.ArchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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