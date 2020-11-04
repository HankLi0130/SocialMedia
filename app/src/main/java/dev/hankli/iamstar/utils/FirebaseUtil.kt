package dev.hankli.iamstar.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Post
import dev.hankli.iamstar.utils.media.MediaForUploading
import io.reactivex.Completable
import io.reactivex.Single
import java.io.InputStream
import java.util.*


object FirebaseUtil {

    const val COLLECTION_POSTS = "Post"
    const val COLLECTION_MEDIAS = "Medias"
    const val BUCKET_POSTS = COLLECTION_POSTS
    const val THUMBNAIL = "thumbnail"

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage by lazy { FirebaseStorage.getInstance() }

    fun getSignInIntent(): Intent {
        val idpConfigs = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("tw").build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(idpConfigs)
            .build()
    }

    fun signOut(context: Context, onCompleted: () -> Unit = {}) {
        AuthUI.getInstance().signOut(context).addOnCompleteListener { onCompleted() }
    }

    /**
     * return Post Object Id
     */
    fun addPost(post: Post, mediasForUploading: List<MediaForUploading>): Single<String> {
        return Single.merge(mediasForUploading.map { uploadPostMedia(it) })
            .toList()
            .flatMap { medias ->
                post.medias = medias
                addPost(post)
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
    private fun addPost(post: Post): Single<String> {
        return Single.create { emitter ->
            val postDoc = db.collection(COLLECTION_POSTS).document()
            post.objectId = postDoc.id
            post.createdAt = Date()
            postDoc.set(post)
                .addOnSuccessListener { emitter.onSuccess(post.objectId) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun updatePost(
        post: Post,
        mediasForUploading: List<MediaForUploading>,
        idsForRemoving: Collection<String>
    ): Completable {
        val newMedias = ArrayList<Media>().apply { this.addAll(post.medias) }
        return Completable.merge(idsForRemoving.map { removePostMedia(it) })
            .andThen(Single.merge(mediasForUploading.map { uploadPostMedia(it) }))
            .toList()
            .flatMapCompletable { medias ->
                newMedias.removeAll { idsForRemoving.contains(it.objectId) }
                newMedias.addAll(medias)
                post.medias = newMedias
                updatePost(post)
            }

//        val mediasForUploading = mediaItems.filter { it.objectId == EMPTY }
//        val uploadActions = mediasForUploading.map { uploadPostMedia(it) }
//
//
//        val removeActions = idsForRemoving.map { removePostMedia(it) }
    }

    private fun updatePost(post: Post): Completable {
        return Completable.create { emitter ->
            post.updatedAt = Date()
            db.collection(COLLECTION_POSTS)
                .document(post.objectId)
                .set(post)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    private fun removePostMedia(mediaId: String): Completable {
        val filePath = "$BUCKET_POSTS/$mediaId"
        val thumbnailPath = "${filePath}_${THUMBNAIL}"
        return Completable.mergeArray(deleteFile(filePath), deleteFile(thumbnailPath))
    }

    fun fetchPost(postId: String): Single<Post> {
        return Single.create { emitter ->
            // get post
            db.collection(COLLECTION_POSTS)
                .document(postId)
                .get()
                .addOnSuccessListener { doc ->
                    doc.toObject(Post::class.java)?.let { post ->
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

    // https://firebase.google.com/docs/storage/android/upload-files
    fun uploadFile(path: String, uri: Uri): Single<String> {
        return Single.create { emitter ->
            val ref = storage.reference.child(path)
            ref.putFile(uri)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { emitter.onSuccess(it.toString()) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun uploadFile(path: String, bytes: ByteArray): Single<String> {
        return Single.create { emitter ->
            val ref = storage.reference.child(path)
            ref.putBytes(bytes)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { emitter.onSuccess(it.toString()) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun uploadFile(path: String, stream: InputStream): Single<String> {
        return Single.create { emitter ->
            val ref = storage.reference.child(path)
            ref.putStream(stream)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { emitter.onSuccess(it.toString()) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    // https://firebase.google.com/docs/storage/android/delete-files#kotlin+ktx
    fun deleteFile(path: String): Completable {
        return Completable.create { emitter ->
            storage.reference.child(path)
                .delete()
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }
}