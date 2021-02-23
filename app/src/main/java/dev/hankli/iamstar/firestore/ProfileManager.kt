package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import dev.hankli.iamstar.data.models.Profile
import kotlinx.coroutines.tasks.await

class ProfileManager(collection: CollectionReference) : FirestoreManager<Profile>(collection) {

    suspend fun getPhotoURL(userId: String): String? {
        return getDoc(userId).get().await().getString(Profile.PHOTO_URL)
    }

    suspend fun updateHeadshot(userId: String, url: String) =
        getDoc(userId).update(Profile.PHOTO_URL, url).await()

    suspend fun updateFcmToken(userId: String, fcmToken: String) =
        getDoc(userId).update(Profile.FCM_TOKEN, fcmToken).await()
}