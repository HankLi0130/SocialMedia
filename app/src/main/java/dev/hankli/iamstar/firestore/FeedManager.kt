package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Reaction
import kotlinx.coroutines.tasks.await
import java.util.*

object FeedManager {
    private const val COLLECTION_FEED = "Feed"
    private const val COLLECTION_REACTIONS = "reactions"
    private const val COLLECTION_COMMENTS = "comments"

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val rootCollection: CollectionReference by lazy { db.collection(COLLECTION_FEED) }

    suspend fun add(feed: Feed) {
        val feedDoc = rootCollection.document()
        feed.objectId = feedDoc.id
        feedDoc.set(feed).await()
    }

    suspend fun update(feed: Feed) {
        feed.updatedAt = Date()
        rootCollection.document(feed.objectId).set(feed).await()
    }

    suspend fun delete(feedId: String) = rootCollection.document(feedId).delete().await()

    fun queryByInfluencer(influencerId: String, limit: Long = 10): Query {
        return rootCollection
            .whereEqualTo("influencer", InfluencerManager.getDoc(influencerId))
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
    }

    suspend fun get(objectId: String): Feed {
        val snapshot = rootCollection.document(objectId).get().await()
        return snapshot.toObject(Feed::class.java)!!
    }

    fun getFeedDocument(feedId: String) = rootCollection.document(feedId)

    private fun getReactionsRef(feedId: String): CollectionReference {
        return rootCollection.document(feedId).collection(COLLECTION_REACTIONS)
    }

    suspend fun hasReaction(feedId: String, user: DocumentReference): Boolean {
        return getReactionsRef(feedId).document(user.id)
            .get()
            .await()
            .exists()
    }

    suspend fun addReaction(feedId: String, user: DocumentReference, reaction: Reaction) {
        getReactionsRef(feedId).document(user.id).set(reaction).await()
    }

    suspend fun removeReaction(feedId: String, user: DocumentReference) {
        getReactionsRef(feedId).document(user.id).delete().await()
    }

    suspend fun removeReactions(feedId: String) {
        val docs = getReactionsRef(feedId).get().await().documents
        for (doc in docs) {
            doc.reference.delete().await()
        }
    }

    suspend fun updateReactionCount(feedId: String) {
        val count = getReactionsRef(feedId).get().await().size()
        rootCollection.document(feedId).update("reactionCount", count).await()
    }

    suspend fun getReaction(feedId: String, user: DocumentReference): Reaction? {
        return getReactionsRef(feedId).document(user.id)
            .get()
            .await()
            .toObject(Reaction::class.java)
    }

    private fun getCommentsRef(feedId: String): CollectionReference {
        return rootCollection.document(feedId).collection(COLLECTION_COMMENTS)
    }

    fun queryComments(feedId: String, limit: Long = 50): Query {
        return getCommentsRef(feedId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
    }

    suspend fun addComment(feedId: String, comment: Comment) {
        val commentDoc = getCommentsRef(feedId).document()
        comment.objectId = commentDoc.id
        commentDoc.set(comment).await()
    }

    suspend fun removeComment(feedId: String, commentId: String) {
        getCommentsRef(feedId).document(commentId).delete().await()
    }

    suspend fun removeComments(feedId: String) {
        val docs = getCommentsRef(feedId).get().await().documents
        for (doc in docs) {
            doc.reference.delete().await()
        }
    }

    suspend fun updateCommentCount(feedId: String) {
        val count = getCommentsRef(feedId).get().await().size()
        rootCollection.document(feedId).update("commentCount", count).await()
    }

    suspend fun updateComment(feedId: String, commentId: String, newContent: String) {
        getCommentsRef(feedId).document(commentId)
            .update(
                mapOf(
                    "content" to newContent,
                    "updatedAt" to Date()
                )
            )
            .await()
    }
}