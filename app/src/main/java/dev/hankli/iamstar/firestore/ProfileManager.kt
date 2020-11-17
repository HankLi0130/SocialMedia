package dev.hankli.iamstar.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.hankli.iamstar.data.models.Profile
import kotlinx.coroutines.tasks.await

object ProfileManager {
    private const val COLLECTION_PROFILE = "Profile"

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val rootCollection: CollectionReference by lazy { db.collection(COLLECTION_PROFILE) }

    fun add(profile: Profile): Task<Void> {
        val profileDoc = rootCollection.document()
        profile.objectId = profileDoc.id
        return profileDoc.set(profile)
    }

    fun update(profile: Profile): Task<Void> {
        return rootCollection.document(profile.objectId).set(profile)
    }

    fun delete(feedId: String): Task<Void> {
        return rootCollection.document(feedId).delete()
    }

    fun getDoc(objectId: String): DocumentReference {
        return rootCollection.document(objectId)
    }

    suspend fun getDocByUid(uid: String): DocumentReference {
        val snapshot = rootCollection.whereEqualTo("uid", uid).get().await()
        return snapshot.documents[0].reference
    }
}