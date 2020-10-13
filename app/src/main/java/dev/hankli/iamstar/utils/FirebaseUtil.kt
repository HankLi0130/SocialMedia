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
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.*

object FirebaseUtil {

    const val COLLECTION_POSTS = "Post"
    const val COLLECTION_MEDIAS = "Medias"
    const val BUCKET_POSTS = COLLECTION_POSTS

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

    fun getPostMediaPath(objectId: String) = "$COLLECTION_POSTS/$objectId/$COLLECTION_MEDIAS"

    fun addPost(post: Post, mediaItems: List<MediaItem>): Completable {
        return addPost(post)
            .flatMapCompletable { objectId ->
                val actions = mediaItems.map { addPostMedia(objectId, it) }
                Completable.merge(actions)
            }
    }

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

    private fun addPostMedia(objectId: String, mediaItem: MediaItem): Completable {
        // get media id from Firestore
        val mediaDoc = db.collection(getPostMediaPath(objectId)).document()
        val mediaId = mediaDoc.id

        // upload file to Firebase Storage
        val fileName = "$mediaId.${mediaItem.ext}"
        val mediaStoragePath = "$BUCKET_POSTS/$fileName"
        return uploadFile(mediaStoragePath, mediaItem.uri!!)
            .map { url ->
                Media(mediaId, url, mediaItem.type, mediaItem.height, mediaItem.width)
            }
            .flatMapCompletable { media ->
                Completable.create { emitter ->
                    mediaDoc.set(media)
                        .addOnSuccessListener { emitter.onComplete() }
                        .addOnFailureListener { emitter.onError(it) }
                }
            }
    }

    fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (e: Exception) -> Unit) {
        post.updatedAt = Date()
        db.collection(COLLECTION_POSTS)
            .document(post.objectId)
            .set(post)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchPostAndMedias(objectId: String): Single<Post> {
        return Single.zip(
            fetchPost(objectId),
            fetchPostMedias(objectId),
            BiFunction { post, medias ->
                post.medias = medias
                return@BiFunction post
            })
    }

    private fun fetchPost(objectId: String): Single<Post> {
        return Single.create { emitter ->
            // get post
            db.collection(COLLECTION_POSTS)
                .document(objectId)
                .get()
                .addOnSuccessListener {
                    val post = it.toObject(Post::class.java)
                    if (post != null) emitter.onSuccess(post)
                    else emitter.onError(Throwable("Post is null."))
                }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    private fun fetchPostMedias(objectId: String): Single<List<Media>> {
        return Single.create { emitter ->
            db.collection(getPostMediaPath(objectId))
                .get()
                .addOnSuccessListener { emitter.onSuccess(it.toObjects(Media::class.java)) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    // https://firebase.google.com/docs/storage/android/upload-files
    fun uploadFile(
        path: String,
        uri: Uri
    ): Single<String> {
        return Single.create { emitter ->
            val ref = storage.reference.child(path)
            ref.putFile(uri)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { emitter.onSuccess(it.toString()) }
                .addOnFailureListener { emitter.onError(it) }
        }
    }
}