package dev.hankli.iamstar.ui.comment

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.repo.FeedRepo
import dev.hankli.iamstar.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentViewModel : BaseViewModel() {

    private val feedRepo = FeedRepo()

    fun sendComment(feedId: String, user: DocumentReference, message: String) {
        if (message.isEmpty()) return

        viewModelScope.launch {
            callProgress(true)
            withContext(Dispatchers.IO) { feedRepo.addComment(feedId, user, message) }
            callProgress(false)
        }
    }
}