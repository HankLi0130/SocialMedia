package dev.hankli.iamstar.repo

import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Reaction
import dev.hankli.iamstar.firebase.StorageManager
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.utils.media.MediaForUploading
import io.reactivex.Completable
import io.reactivex.Single

class FeedRepo {

    companion object {
        private const val BUCKET_FEED = "Feed"
        private const val THUMBNAIL = "thumbnail"
    }

    fun addFeed(feed: Feed, mediasForUploading: List<MediaForUploading>): Single<String> {
        return Single.merge(mediasForUploading.map { uploadFeedMedia(it) })
            .toList()
            .flatMap { medias ->
                feed.medias = medias
                FeedManager.add(feed)
            }
    }

    fun updateFeed(
        feed: Feed,
        mediasForUploading: List<MediaForUploading>,
        idsForRemoving: Collection<String>
    ): Completable {
        val newMedias = ArrayList<Media>().apply { this.addAll(feed.medias) }
        return Completable.merge(idsForRemoving.map { removeFeedMedia(it) })
            .andThen(Single.merge(mediasForUploading.map { uploadFeedMedia(it) }))
            .toList()
            .flatMapCompletable { medias ->
                newMedias.removeAll { idsForRemoving.contains(it.objectId) }
                newMedias.addAll(medias)
                feed.medias = newMedias
                FeedManager.update(feed)
            }
    }

    fun deleteFeed(feedId: String): Completable {
        return FeedManager.retrieve(feedId)
            .flatMapCompletable { feed ->
                Completable.merge(feed.medias.map { removeFeedMedia(it.objectId) })
            }
            .andThen(FeedManager.delete(feedId))
    }

    suspend fun fetchFeed(postId: String): Feed {
        return FeedManager.get(postId)
    }

    private fun uploadFeedMedia(media: MediaForUploading): Single<Media> {
        val filePath = "${BUCKET_FEED}/${media.objectId}"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"
        return Single.zip<String, String, Media>(
            StorageManager.uploadFile(filePath, media.file),
            StorageManager.uploadFile(thumbnailPath, media.thumbnail)
        ) { fileUrl, thumbnailUrl ->
            Media(media.objectId, fileUrl, media.type, media.width, media.height, thumbnailUrl)
        }
    }

    private fun removeFeedMedia(mediaId: String): Completable {
        val filePath = "$BUCKET_FEED/$mediaId"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"
        return Completable.create { emitter ->
            StorageManager.deleteFile(filePath)
                .continueWith { if (it.isSuccessful) StorageManager.deleteFile(thumbnailPath) }
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    suspend fun hasReaction(feedId: String, user: DocumentReference): Boolean {
        return FeedManager.hasReaction(feedId, user)
    }

    suspend fun like(feedId: String, user: DocumentReference) {
        val reaction = Reaction(user.id, "like", user)
        FeedManager.addReaction(feedId, user, reaction)
        FeedManager.updateReactionCount(feedId)
    }

    suspend fun unlike(feedId: String, user: DocumentReference) {
        FeedManager.removeReaction(feedId, user)
        FeedManager.updateReactionCount(feedId)
    }

    suspend fun getReaction(feedId: String, user: DocumentReference): Reaction? {
        return FeedManager.getReaction(feedId, user)
    }

    suspend fun addComment(feedId: String, user: DocumentReference, message: String) {
        val comment = Comment(profile = user, content = message)
        FeedManager.addComment(feedId, comment)
        FeedManager.updateCommentCount(feedId)
    }
}