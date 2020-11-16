package dev.hankli.iamstar.ui.home

import android.util.Log
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.BaseViewModel
import dev.hankli.iamstar.utils.FirebaseUtil
import dev.hankli.iamstar.utils.FirebaseUtil.COLLECTION_POSTS
import dev.hankli.iamstar.utils.FirebaseUtil.auth
import dev.hankli.iamstar.utils.FirebaseUtil.db
import io.reactivex.rxkotlin.addTo

class HomeViewModel : BaseViewModel() {

    fun fetchUserPosts() {
        val userId = auth.currentUser!!.uid
        db.collection(COLLECTION_POSTS)
            .whereEqualTo("authorID", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                value?.let {
                    val posts = it.toObjects(Post::class.java)
                    Log.i("test", posts.toString())
                }
            }
    }

    fun deletePost(objectId: String) {
        showProgress()
        FirebaseUtil.deletePost(objectId)
            .doOnComplete { dismissProgress() }
            .subscribe({

            }, {
                showAlert(R.string.alert_delete_post_failed)
            })
            .addTo(disposables)
    }

    fun onPostCardReactionClick(objectId: String, reactionType: String, checked: Boolean) {

    }
}