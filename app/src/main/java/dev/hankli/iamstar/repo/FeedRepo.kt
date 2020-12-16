package dev.hankli.iamstar.repo

import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Reaction
import dev.hankli.iamstar.firebase.StorageManager
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.utils.ext.toByteArray
import dev.hankli.iamstar.utils.media.UploadingMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class FeedRepo {

    companion object {
        private const val BUCKET_FEED = "Feed"
        private const val THUMBNAIL = "thumbnail"
    }

    suspend fun addFeed(scope: CoroutineScope, feed: Feed, uploadingMedias: List<UploadingMedia>) {
        val medias = uploadingMedias.map { uploadFeedMedia(scope, it) }
        feed.medias = medias
        FeedManager.add(feed)
    }

    suspend fun updateFeed(
        scope: CoroutineScope,
        feed: Feed,
        uploadingMedias: List<UploadingMedia>,
        removingMediaIds: Set<String>
    ) {
        val newMedias = uploadingMedias.map { uploadFeedMedia(scope, it) }

        val medias = feed.medias.toMutableList()
        removingMediaIds.forEach { mediaId ->
            removeFeedMedia(mediaId)
            medias.removeIf { it.objectId == mediaId }
        }

        medias.addAll(newMedias)
        feed.medias = medias
        FeedManager.update(feed)
    }

//    fun updateFeed(
//        feed: Feed,
//        mediasForUploading: List<MediaForUploading>,
//        idsForRemoving: Collection<String>
//    ): Completable {
//        val newMedias = ArrayList<Media>().apply { this.addAll(feed.medias) }
//        return Completable.merge(idsForRemoving.map { removeFeedMedia(it) })
//            .andThen(Single.merge(mediasForUploading.map { uploadFeedMedia(it) }))
//            .toList()
//            .flatMapCompletable { medias ->
//                newMedias.removeAll { idsForRemoving.contains(it.objectId) }
//                newMedias.addAll(medias)
//                feed.medias = newMedias
//                FeedManager.update(feed)
//            }
//    }

    suspend fun removeFeed(feedId: String) {
        FeedManager.removeComments(feedId)
        FeedManager.removeReactions(feedId)

        val feed = fetchFeed(feedId)
        for (media in feed.medias) {
            removeFeedMedia(media.objectId)
        }

        FeedManager.delete(feedId)
    }

    suspend fun fetchFeed(postId: String) = FeedManager.get(postId)

    private suspend fun uploadFeedMedia(scope: CoroutineScope, media: UploadingMedia): Media {
        val filePath = "${BUCKET_FEED}/${media.objectId}"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"

        val fileUrl = scope.async {
            StorageManager.uploadFile(filePath, media.file)
        }

        val thumbnailUrl = scope.async {
            StorageManager.uploadFile(thumbnailPath, media.thumbnail.toByteArray())
        }

        return Media(
            media.objectId,
            fileUrl.await(),
            media.type,
            media.width,
            media.height,
            thumbnailUrl.await()
        )
    }

    private suspend fun removeFeedMedia(mediaId: String) {
        val filePath = "$BUCKET_FEED/$mediaId"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"
        StorageManager.deleteFile(filePath)
        StorageManager.deleteFile(thumbnailPath)
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