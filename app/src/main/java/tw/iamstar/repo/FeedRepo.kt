package tw.iamstar.repo

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import tw.iamstar.BuildConfig
import tw.iamstar.data.enums.ReactionType
import tw.iamstar.data.models.firestore.Comment
import tw.iamstar.data.models.firestore.Feed
import tw.iamstar.data.models.firestore.Media
import tw.iamstar.data.models.firestore.Reaction
import tw.iamstar.data.models.messaging.FeedData
import tw.iamstar.data.models.messaging.MessagingKey
import tw.iamstar.firebase.BUCKET_FEED
import tw.iamstar.firebase.StorageManager
import tw.iamstar.firebase.THUMBNAIL
import tw.iamstar.firestore.FeedManager
import tw.iamstar.firestore.InfluencerManager
import tw.iamstar.firestore.ProfileManager
import tw.iamstar.network.FcmApi
import tw.iamstar.network.NotificationRequest
import tw.iamstar.utils.Consts.MESSAGING_KEY
import tw.iamstar.utils.Consts.MESSAGING_VALUE
import tw.iamstar.utils.media.UploadingMedia
import java.util.*

class FeedRepo(
    private val feedManager: FeedManager,
    private val influencerManager: InfluencerManager,
    private val profileManager: ProfileManager,
    private val fcmApi: FcmApi,
    private val moshi: Moshi
) {

    suspend fun addFeed(
        scope: CoroutineScope,
        feed: Feed,
        authorUserId: String,
        influencerId: String,
        uploadingMedias: List<UploadingMedia>
    ) {
        feed.author = profileManager.getDoc(authorUserId)
        feed.influencer = influencerManager.getDoc(influencerId)
        val medias = uploadingMedias.map { uploadFeedMedia(scope, it) }
        feed.medias = medias
        feedManager.add(feed)
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

        feed.updatedAt = Date()
        feedManager.set(feed)
    }

    suspend fun removeFeed(feedId: String) {
        feedManager.getCommentManager(feedId).removeAll()
        feedManager.getReactionManager(feedId).removeAll()

        val feed = getFeed(feedId)!!
        for (media in feed.medias) {
            removeFeedMedia(media.objectId)
        }

        feedManager.remove(feedId)
    }

    suspend fun getFeed(feedId: String) = feedManager.get(feedId, Feed::class.java)

    fun getFeedDoc(feedId: String) = feedManager.getDoc(feedId)

    fun observeFeed(
        feedId: String,
        listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit
    ) = feedManager.observeDoc(feedId, listener)

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

    suspend fun hasReaction(feedId: String, userId: String): Boolean {
        return feedManager.getReactionManager(feedId).exists(userId)
    }

    suspend fun like(feedId: String, userId: String) {
        val userDoc = profileManager.getDoc(userId)
        val reaction = Reaction(userId, ReactionType.LIKE, userDoc)
        feedManager.getReactionManager(feedId).set(reaction)

        // TODO Do on server
        //feedManager.updateReactionCount(feedId)
    }

    suspend fun unlike(feedId: String, userId: String) {
        feedManager.getReactionManager(feedId).remove(userId)

        // TODO Do it on server
        //feedManager.updateReactionCount(feedId)
    }

    suspend fun getReaction(feedId: String, userId: String): Reaction? {
        return feedManager.getReactionManager(feedId).get(userId, Reaction::class.java)
    }

    suspend fun addComment(feedId: String, userId: String, message: String) {
        val userDoc = profileManager.getDoc(userId)
        val comment = Comment(profile = userDoc, content = message)
        feedManager.getCommentManager(feedId).add(comment)

        // TODO Do it on server
        feedManager.updateCommentCount(feedId)
    }

    fun queryByInfluencer(influencerId: String): Query {
        return feedManager.queryByInfluencer(influencerManager.getDoc(influencerId))
    }

    fun queryComments(feedId: String): Query {
        return feedManager.getCommentManager(feedId).queryComments()
    }

    suspend fun sendToChannel(data: FeedData) {
        val topic = BuildConfig.APPLICATION_ID
        val value = moshi.adapter(FeedData::class.java).toJson(data)
        val request = NotificationRequest(
            topic, mapOf(
                MESSAGING_KEY to MessagingKey.KEY_FEED.name,
                MESSAGING_VALUE to value
            )
        )
        fcmApi.sendToChannel(request)
    }
}