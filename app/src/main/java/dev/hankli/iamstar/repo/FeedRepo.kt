package dev.hankli.iamstar.repo

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.firebase.StorageManager
import dev.hankli.iamstar.firebase.StorageManager.deleteFile
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

    fun getFeeds(influencer: DocumentReference): Query {
        return FeedManager.getQuery(influencer)
    }

    fun uploadFeedMedia(media: MediaForUploading): Single<Media> {
        val filePath = "${BUCKET_FEED}/${media.objectId}"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"
        return Single.zip<String, String, Media>(
            StorageManager.uploadFile(filePath, media.file),
            StorageManager.uploadFile(thumbnailPath, media.thumbnail)
        ) { fileUrl, thumbnailUrl ->
            Media(media.objectId, fileUrl, media.type, media.width, media.height, thumbnailUrl)
        }
    }

    fun removeFeedMedia(mediaId: String): Completable {
        val filePath = "$BUCKET_FEED/$mediaId"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"
        return Completable.create { emitter ->
            deleteFile(filePath)
                .continueWith {
                    if (it.isSuccessful) deleteFile(thumbnailPath)
                }
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }
}