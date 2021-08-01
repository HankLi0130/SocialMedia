package app.hankdev.firestore

import app.hankdev.data.models.firestore.Feed
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


class FeedManager(collection: CollectionReference) : FirestoreManager<Feed>(collection) {

    fun queryAll() = rootCollection.orderBy(Feed.CREATED_AT, Query.Direction.DESCENDING)

    fun query(limit: Long) = rootCollection
        .orderBy(Feed.UPDATED_AT, Query.Direction.DESCENDING)
        .limit(limit)

    fun getCommentManager(feedId: String): CommentManager {
        return CommentManager(getDoc(feedId).collection(COLLECTION_COMMENTS))
    }

    fun getReactionManager(feedId: String): ReactionManager {
        return ReactionManager(getDoc(feedId).collection(COLLECTION_REACTIONS))
    }

    suspend fun updateReactionCount(feedId: String) {
        val count = getReactionManager(feedId).getDocSize()
        getDoc(feedId).update(Feed.REACTION_COUNT, count).await()
    }


    suspend fun updateCommentCount(feedId: String) {
        val count = getCommentManager(feedId).getDocSize()
        getDoc(feedId).update(Feed.COMMENT_COUNT, count).await()
    }
}