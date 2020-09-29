package dev.hankli.iamstar.utils

import android.content.Context
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dev.hankli.iamstar.data.models.Post
import java.util.*

object FirebaseUtil {

    const val COLLECTION_POSTS = "Post"

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

    fun addPost(post: Post, listener: OnCompleteListener<Void>) {
        val doc = db.collection(COLLECTION_POSTS).document()
        post.objectId = doc.id
        post.createdAt = Date()
        doc.set(post).addOnCompleteListener(listener)
    }

    fun updatePost(post: Post, listener: OnCompleteListener<Void>) {
        post.updatedAt = Date()
        db.collection(COLLECTION_POSTS)
            .document(post.objectId)
            .set(post)
            .addOnCompleteListener(listener)
    }
}