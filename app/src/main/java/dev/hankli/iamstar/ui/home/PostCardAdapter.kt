package dev.hankli.iamstar.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.FirebaseUtil.COLLECTION_POSTS
import dev.hankli.iamstar.utils.FirebaseUtil.auth
import dev.hankli.iamstar.utils.FirebaseUtil.db

class PostCardAdapter : FirestoreRecyclerAdapter<Post, PostCardViewHolder>(options) {

    var onItemOptionsClick: (objectId: String) -> Unit = {}

    companion object {
        private val userId = auth.currentUser!!.uid

        private val query = db.collection(COLLECTION_POSTS)
            .whereEqualTo("authorId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        private val options = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(PostCardViewHolder.layoutRes, parent, false)
        return PostCardViewHolder(view)
    }

    override fun onBindViewHolder(holderCard: PostCardViewHolder, position: Int, model: Post) {
        holderCard.bind(position, model, onItemOptionsClick)
    }
}