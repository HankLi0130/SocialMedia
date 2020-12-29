package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.*
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.firebase.AuthManager
import kotlinx.coroutines.tasks.await

object ProfileManager {
    private const val COLLECTION_PROFILE = "Profile"

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val rootCollection: CollectionReference by lazy { db.collection(COLLECTION_PROFILE) }

    fun getCurrentUserDoc() = getDoc(AuthManager.currentUserId!!)

    fun getDoc(userId: String) = rootCollection.document(userId)

    suspend fun updateHeadshot(userId: String, url: String) =
        getDoc(userId).update("photoURL", url).await()

    suspend fun update(profile: Profile) = getDoc(profile.objectId).set(profile).await()

    suspend fun delete(userId: String) = getDoc(userId).delete().await()

    suspend fun get(userId: String) = getDoc(userId).get().await()

    fun addSnapshotListener(
        objectId: String,
        listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit
    ): ListenerRegistration {
        return rootCollection.document(objectId).addSnapshotListener(listener)
    }
}