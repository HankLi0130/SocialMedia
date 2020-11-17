package dev.hankli.iamstar.utils

import com.google.firebase.firestore.FirebaseFirestore
import dev.hankli.iamstar.data.models.Feed
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.firebase.StorageManager.uploadFile
import dev.hankli.iamstar.utils.media.MediaForUploading
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*


object FirebaseUtil {
    const val COLLECTION_POSTS = ""
    const val BUCKET_POSTS = COLLECTION_POSTS
    const val THUMBNAIL = "thumbnail"
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * return Post Object Id
     */
    fun addPost(feed: Feed, mediasForUploading: List<MediaForUploading>): Single<String> {
        return Single.merge(mediasForUploading.map { uploadPostMedia(it) })
            .toList()
            .flatMap { medias ->
                feed.medias = medias
                addPost(feed)
            }
    }

    private fun uploadPostMedia(media: MediaForUploading): Single<Media> {
        val filePath = "$BUCKET_POSTS/${media.objectId}"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"
        return Single.zip<String, String, Media>(
            uploadFile(filePath, media.file),
            uploadFile(thumbnailPath, media.thumbnail)
        ) { fileUrl, thumbnailUrl ->
            Media(media.objectId, fileUrl, media.type, media.width, media.height, thumbnailUrl)
        }
    }

    /**
     * return Post Object Id
     */
    private fun addPost(feed: Feed): Single<String> {
        return Single.create { emitter ->
            val postDoc = db.collection(COLLECTION_POSTS).document()
            feed.objectId = postDoc.id
            feed.createdAt = Date()
            postDoc.set(feed)
                .addOnSuccessListener { emitter.onSuccess(feed.objectId) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun updatePost(
        feed: Feed,
        mediasForUploading: List<MediaForUploading>,
        idsForRemoving: Collection<String>
    ): Completable {
        val newMedias = ArrayList<Media>().apply { this.addAll(feed.medias) }
        return Completable.merge(idsForRemoving.map { removePostMedia(it) })
            .andThen(Single.merge(mediasForUploading.map { uploadPostMedia(it) }))
            .toList()
            .flatMapCompletable { medias ->
                newMedias.removeAll { idsForRemoving.contains(it.objectId) }
                newMedias.addAll(medias)
                feed.medias = newMedias
                updatePost(feed)
            }
    }

    private fun updatePost(feed: Feed): Completable {
        return Completable.create { emitter ->
            feed.updatedAt = Date()
            db.collection(COLLECTION_POSTS)
                .document(feed.objectId)
                .set(feed)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    private fun removePostMedia(mediaId: String): Completable {
        val filePath = "$BUCKET_POSTS/$mediaId"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"
        //return Completable.mergeArray(deleteFile(filePath), deleteFile(thumbnailPath))
        return Completable.complete()
    }

    fun fetchPost(postId: String): Single<Feed> {
        return Single.create { emitter ->
            // get post
            db.collection(COLLECTION_POSTS)
                .document(postId)
                .get()
                .addOnSuccessListener { doc ->
                    doc.toObject(Feed::class.java)?.let { post ->
                        emitter.onSuccess(post)
                    } ?: emitter.onError(Throwable("Post is null."))
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun deletePost(postId: String): Completable {
        return fetchPost(postId)
            .flatMapCompletable { post ->
                Completable.merge(post.medias.map { removePostMedia(it.objectId) })
            }
            .andThen(Completable.create { emitter ->
                db.collection(COLLECTION_POSTS)
                    .document(postId)
                    .delete()
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
            })
    }
}