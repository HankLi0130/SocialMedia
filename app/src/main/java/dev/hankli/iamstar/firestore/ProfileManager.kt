package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.*
import dev.hankli.iamstar.data.models.Profile
import kotlinx.coroutines.tasks.await

object ProfileManager {
    private const val COLLECTION_PROFILE = "Profile"

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val rootCollection: CollectionReference by lazy { db.collection(COLLECTION_PROFILE) }

    suspend fun updateHeadshot(userId: String, url: String) =
        rootCollection.document(userId).update("photoURL", url).await()

    suspend fun update(profile: Profile) =
        rootCollection.document(profile.objectId).set(profile).await()

    suspend fun delete(feedId: String) = rootCollection.document(feedId).delete().await()

    suspend fun get(objectId: String) =
        rootCollection.document(objectId).get().await().toObject(Profile::class.java)

    fun getDoc(objectId: String) = rootCollection.document(objectId)

    fun addSnapshotListener(
        objectId: String,
        listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit
    ): ListenerRegistration {
        return rootCollection.document(objectId).addSnapshotListener(listener)
    }
}