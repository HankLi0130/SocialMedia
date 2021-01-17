package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.*
import dev.hankli.iamstar.data.models.Profile
import kotlinx.coroutines.tasks.await

class ProfileManager(collection: CollectionReference) : FirestoreManager<Profile>(collection) {

    suspend fun getPhotoURL(userId: String): String? {
        return getDoc(userId).get().await().getString(Profile.PHOTO_URL)
    }

    suspend fun updateHeadshot(userId: String, url: String) =
        getDoc(userId).update(Profile.PHOTO_URL, url).await()

//    fun addSnapshotListener(
//        objectId: String,
//        listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit
//    ): ListenerRegistration {
//        return rootCollection.document(objectId).addSnapshotListener(listener)
//    }
}