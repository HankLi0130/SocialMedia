package dev.hankli.iamstar.repo

import dev.hankli.iamstar.data.models.Comment
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Reaction
import dev.hankli.iamstar.firebase.StorageManager
import dev.hankli.iamstar.firestore.FeedManager
import dev.hankli.iamstar.firestore.InfluencerManager
import dev.hankli.iamstar.firestore.ProfileManager
import dev.hankli.iamstar.utils.media.UploadingMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class FeedRepo {

    companion object {
        private const val BUCKET_FEED = "Feed"
        private const val THUMBNAIL = "thumbnail"
    }

    suspend fun addFeed(
        scope: CoroutineScope,
        feed: Feed,
        influencerId: String,
        uploadingMedias: List<UploadingMedia>
    ) {
        feed.author = ProfileManager.getCurrentUserDoc()
        feed.influencer = InfluencerManager.getDoc(influencerId)
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
            StorageManager.uploadFile(thumbnailPath, media.thumbnail)
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

    suspend fun hasReaction(feedId: String): Boolean {
        return FeedManager.hasReaction(feedId, ProfileManager.getCurrentUserDoc())
    }

    suspend fun like(feedId: String) {
        val user = ProfileManager.getCurrentUserDoc()
        val reaction = Reaction(user.id, "like", user)
        FeedManager.addReaction(feedId, user, reaction)
        FeedManager.updateReactionCount(feedId)
    }

    suspend fun unlike(feedId: String) {
        FeedManager.removeReaction(feedId, ProfileManager.getCurrentUserDoc())
        FeedManager.updateReactionCount(feedId)
    }

    suspend fun getReaction(feedId: String): Reaction? {
        return FeedManager.getReaction(feedId, ProfileManager.getCurrentUserDoc())
    }

    suspend fun addComment(feedId: String, message: String) {
        val comment = Comment(profile = ProfileManager.getCurrentUserDoc(), content = message)
        FeedManager.addComment(feedId, comment)
        FeedManager.updateCommentCount(feedId)
    }
}