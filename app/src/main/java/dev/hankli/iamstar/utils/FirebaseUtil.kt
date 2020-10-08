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

    fun getPostMediaIds(
        postId: String,
        mediaCount: Int
    ): Array<String> {
        val path = "$COLLECTION_POSTS/$postId/$COLLECTION_MEDIAS"
        val mediaCollection = db.collection(path)
        return Array(mediaCount) { mediaCollection.document().id }
    }

    // https://firebase.google.com/docs/storage/android/upload-files
    fun uploadPostMedia(
        name: String,
        uri: Uri,
        onSuccess: (url: String) -> Unit,
        onFailure: (e: Exception) -> Unit
    ) {
        val ref = storage.reference.child("$BUCKET_POSTS/$name")
        ref.putFile(uri)
            .continueWithTask { ref.downloadUrl }
            .addOnSuccessListener { onSuccess(it.toString()) }
            .addOnFailureListener { onFailure(it) }
    }

    fun updatePostMedia(
        postId: String,
        media: Media,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit
    ) {
        val mediaDoc = db.collection("$COLLECTION_POSTS/$postId/$COLLECTION_MEDIAS")
            .document(media.objectId)
        mediaDoc.set(media)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
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
}