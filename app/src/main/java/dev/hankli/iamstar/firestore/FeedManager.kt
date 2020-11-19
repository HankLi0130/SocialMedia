package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Reaction
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.tasks.await
import java.util.*

object FeedManager {
    private const val COLLECTION_FEED = "Feed"
    private const val COLLECTION_REACTIONS = "reactions"

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

    fun queryByInfluencer(influencer: DocumentReference, limit: Long = 50): Query {
        return rootCollection
            .whereEqualTo("influencer", influencer)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
    }

    suspend fun get(objectId: String): Feed {
        val snapshot = rootCollection.document(objectId).get().await()
        return snapshot.toObject(Feed::class.java)!!
    }

    private fun getReactions(feedId: String): CollectionReference {
        return rootCollection.document(feedId).collection(COLLECTION_REACTIONS)
    }

    suspend fun hasReaction(feedId: String, user: DocumentReference): Boolean {
        val snapshot = getReactions(feedId)
            .whereEqualTo("profile", user)
            .limit(1L)
            .get()
            .await()

        return !snapshot.isEmpty
    }

    suspend fun addReaction(feedId: String, user: DocumentReference) {
        val reaction = Reaction(user.id, "like", user)
        getReactions(feedId).document(user.id).set(reaction).await()
    }

    suspend fun removeReaction(feedId: String, user: DocumentReference) {
        getReactions(feedId).document(user.id).delete().await()
    }

    fun queryCollectionGroup() {
        //db.collectionGroup()
    }
}