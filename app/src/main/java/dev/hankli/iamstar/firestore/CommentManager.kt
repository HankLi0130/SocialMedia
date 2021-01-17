package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Comment
import kotlinx.coroutines.tasks.await

class CommentManager(collection: CollectionReference) : FirestoreManager<Comment>(collection) {

    fun queryComments(limit: Long = 50) = rootCollection
        .orderBy(Comment.CREATED_AT, Query.Direction.DESCENDING)
        .limit(limit)

    suspend fun removeAll() {
        val docs = rootCollection.get().await().documents
        for (doc in docs) doc.reference.delete().await()
    }

    suspend fun count() = rootCollection.get().await().size()
}