package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Reaction
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.tasks.await
import tw.hankli.brookray.constant.ZERO
import java.util.*

object FeedManager {
    private const val COLLECTION_FEED = "Feed"
    private const val COLLECTION_REACTIONS = "reactions"
    private const val COLLECTION_COMMENTS = "comments"

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val rootCollection: CollectionReference by lazy { db.collection(COLLECTION_FEED) }

    fun add(feed: Feed): Single<String> {
        return Single.create { emitter ->
            val feedDoc = rootCollection.document()
            feed.objectId = feedDoc.id
            feedDoc.set(feed)
                .addOnSuccessListener { emitter.onSuccess(feed.objectId) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun update(feed: Feed): Completable {
        return Completable.create { emitter ->
            feed.updatedAt = Date()
            rootCollection.document(feed.objectId).set(feed)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun delete(feedId: String): Completable {
        return Completable.create { emitter ->
            rootCollection.document(feedId).delete()
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun retrieve(objectId: String): Single<Feed> {
        return Single.create { emitter ->
            rootCollection.document(objectId)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.toObject(Feed::class.java)?.let {
                        emitter.onSuccess(it)
                    } ?: emitter.onError(NullPointerException("Feed is null !"))
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun queryByInfluencer(influencer: DocumentReference, limit: Long = 10): Query {
        return rootCollection
            .whereEqualTo("influencer", influencer)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
    }

    suspend fun get(objectId: String): Feed {
        val snapshot = rootCollection.document(objectId).get().await()
        return snapshot.toObject(Feed::class.java)!!
    }

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

    suspend fun increaseReactionCount(feedId: String) {
        val feedDoc = rootCollection.document(feedId)
        val count = feedDoc.get().await().getLong("reactionCount")?.toInt() ?: ZERO
        feedDoc.update("reactionCount", count + 1).await()
    }

    suspend fun removeReaction(feedId: String, user: DocumentReference) {
        getReactionsRef(feedId).document(user.id).delete().await()
    }

    suspend fun reduceReactionCount(feedId: String) {
        val feedDoc = rootCollection.document(feedId)
        val count = feedDoc.get().await().getLong("reactionCount")?.toInt() ?: ZERO
        feedDoc.update("reactionCount", count - 1).await()
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

    suspend fun increaseCommentCount(feedId: String) {
        val feedDoc = rootCollection.document(feedId)
        val count = feedDoc.get().await().getLong("commentCount")?.toInt() ?: ZERO
        feedDoc.update("commentCount", count + 1).await()
    }

    suspend fun removeComment(feedId: String, commentId: String) {
        getCommentsRef(feedId).document(commentId).delete().await()
    }

    suspend fun reduceCommentCount(feedId: String) {
        val feedDoc = rootCollection.document(feedId)
        val count = feedDoc.get().await().getLong("commentCount")?.toInt() ?: ZERO
        feedDoc.update("commentCount", count - 1).await()
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