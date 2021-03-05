package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import tw.iamstar.data.models.Feed

class FeedManager(collection: CollectionReference) : FirestoreManager<Feed>(collection) {

    fun queryByInfluencer(influencerDoc: DocumentReference, limit: Long = 10): Query {
        return rootCollection
            .whereEqualTo(Feed.INFLUENCER, influencerDoc)
            .orderBy(Feed.CREATED_AT, Query.Direction.DESCENDING)
            .limit(limit)
    }

    fun getCommentManager(feedId: String): CommentManager {
        return CommentManager(getDoc(feedId).collection(COLLECTION_COMMENTS))
    }

    fun getReactionManager(feedId: String): ReactionManager {
        return ReactionManager(getDoc(feedId).collection(COLLECTION_REACTIONS))
    }

//    private fun getReactionsRef(feedId: String): CollectionReference {
//        return rootCollection.document(feedId).collection(COLLECTION_REACTIONS)
//    }

//    suspend fun hasReaction(feedId: String, user: DocumentReference): Boolean {
//        return getReactionsRef(feedId).document(user.id)
//            .get()
//            .await()
//            .exists()
//    }

//    suspend fun addReaction(feedId: String, user: DocumentReference, reaction: Reaction) {
//        getReactionsRef(feedId).document(user.id).set(reaction).await()
//    }

//    suspend fun removeReaction(feedId: String, user: DocumentReference) {
//        getReactionsRef(feedId).document(user.id).delete().await()
//    }

//    suspend fun removeReactions(feedId: String) {
//        val docs = getReactionsRef(feedId).get().await().documents
//        for (doc in docs) {
//            doc.reference.delete().await()
//        }
//    }

    suspend fun updateReactionCount(feedId: String) {
        val count = getReactionManager(feedId).getDocSize()
        rootCollection.document(feedId).update(Feed.REACTION_COUNT, count).await()
    }

//    suspend fun getReaction(feedId: String, user: DocumentReference): Reaction? {
//        return getReactionsRef(feedId).document(user.id)
//            .get()
//            .await()
//            .toObject(Reaction::class.java)
//    }

//    private fun getCommentsRef(feedId: String): CollectionReference {
//        return rootCollection.document(feedId).collection(COLLECTION_COMMENTS)
//    }

//    fun queryComments(feedId: String, limit: Long = 50): Query {
//        return getCommentsRef(feedId)
//            .orderBy("createdAt", Query.Direction.DESCENDING)
//            .limit(limit)
//    }

//    suspend fun addComment(feedId: String, comment: Comment) {
//        val commentDoc = getCommentsRef(feedId).document()
//        comment.objectId = commentDoc.id
//        commentDoc.set(comment).await()
//    }

//    suspend fun removeComment(feedId: String, commentId: String) {
//        getCommentsRef(feedId).document(commentId).delete().await()
//    }

//    suspend fun removeComments(feedId: String) {
//        val docs = getCommentsRef(feedId).get().await().documents
//        for (doc in docs) {
//            doc.reference.delete().await()
//        }
//    }

    suspend fun updateCommentCount(feedId: String) {
        val count = getCommentManager(feedId).getDocSize()
        rootCollection.document(feedId).update(Feed.COMMENT_COUNT, count).await()
    }

//    suspend fun updateComment(feedId: String, commentId: String, newContent: String) {
//        getCommentsRef(feedId).document(commentId)
//            .update(
//                mapOf(
//                    "content" to newContent,
//                    "updatedAt" to Date()
//                )
//            )
//            .await()
//    }
}