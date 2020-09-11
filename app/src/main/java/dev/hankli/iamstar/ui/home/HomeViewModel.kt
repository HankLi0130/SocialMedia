package dev.hankli.iamstar.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.FirebaseUtil.COLLECTION_POSTS
import dev.hankli.iamstar.utils.FirebaseUtil.auth
import dev.hankli.iamstar.utils.FirebaseUtil.db

class HomeViewModel : ViewModel() {

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
}