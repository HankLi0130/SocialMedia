package dev.hankli.iamstar.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Post
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

    fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (e: Exception) -> Unit) {
        val postDoc = db.collection(COLLECTION_POSTS).document()
        post.objectId = postDoc.id
        post.createdAt = Date()
        postDoc.set(post)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun addPostMedia(
        postId: String,
        mediaItem: MediaItem,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit
    ) {
        // get id
        val mediaCollectionPath = "$COLLECTION_POSTS/$postId/$COLLECTION_MEDIAS"
        val mediaDoc = db.collection(mediaCollectionPath).document()
        val mediaId = mediaDoc.id

        // upload file
        val fileName = "$mediaId.${mediaItem.ext}"
        val mediaStoragePath = "$BUCKET_POSTS/$fileName"
        uploadFile(mediaStoragePath, mediaItem.uri!!,
            // Success
            { url ->
                // update media
                val media = Media(mediaId, url, mediaItem.type, mediaItem.height, mediaItem.width)
                mediaDoc.set(media)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { ex -> onFailure(ex) }
            },
            // Failure
            { ex -> onFailure(ex) })
    }

    fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (e: Exception) -> Unit) {
        post.updatedAt = Date()
        db.collection(COLLECTION_POSTS)
            .document(post.objectId)
            .set(post)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchPost(
        objectId: String,
        onSuccessListener: OnSuccessListener<in DocumentSnapshot>,
        onFailureListener: OnFailureListener
    ) {
        db.collection(COLLECTION_POSTS)
            .document(objectId)
            .get()
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    // https://firebase.google.com/docs/storage/android/upload-files
    fun uploadFile(
        path: String,
        uri: Uri,
        onSuccess: (url: String) -> Unit,
        onFailure: (e: Exception) -> Unit
    ) {
        val ref = storage.reference.child(path)
        ref.putFile(uri)
            .continueWithTask { ref.downloadUrl }
            .addOnSuccessListener { onSuccess(it.toString()) }
            .addOnFailureListener { onFailure(it) }
    }
}