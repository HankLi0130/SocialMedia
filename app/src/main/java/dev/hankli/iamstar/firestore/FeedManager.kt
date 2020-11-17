package dev.hankli.iamstar.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Feed
import io.reactivex.Single
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
            feed.createdAt = Date()
            feedDoc.set(feed)
                .addOnSuccessListener { emitter.onSuccess(feed.objectId) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun update(feed: Feed): Task<Void> {
        feed.updatedAt = Date()
        return rootCollection.document(feed.objectId).set(feed)
    }

    fun delete(feedId: String): Task<Void> {
        return rootCollection.document(feedId).delete()
    }

    fun getQuery(influencer: DocumentReference): Query {
        return rootCollection
            .whereEqualTo("influencer", influencer)
            .orderBy("createdAt", Query.Direction.DESCENDING)
    }
}