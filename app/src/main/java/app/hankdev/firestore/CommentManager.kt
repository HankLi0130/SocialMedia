package app.hankdev.firestore

import app.hankdev.data.models.firestore.Comment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class CommentManager(collection: CollectionReference) : FirestoreManager<Comment>(collection) {

    fun queryComments(limit: Long = 50) = rootCollection
        .orderBy(Comment.CREATED_AT, Query.Direction.DESCENDING)
        .limit(limit)

    suspend fun removeAll() {
        val docs = rootCollection.get().await().documents
        for (doc in docs) doc.reference.delete().await()
    }
}