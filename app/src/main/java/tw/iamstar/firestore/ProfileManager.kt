package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import tw.iamstar.data.models.Profile

class ProfileManager(collection: CollectionReference) : FirestoreManager<Profile>(collection) {

    suspend fun getPhotoURL(userId: String): String? {
        return getDoc(userId).get().await().getString(Profile.PHOTO_URL)
    }

    suspend fun updateHeadshot(userId: String, url: String) =
        getDoc(userId).update(Profile.PHOTO_URL, url).await()

    suspend fun updateFcmToken(userId: String, fcmToken: String) =
        getDoc(userId).update(Profile.FCM_TOKEN, fcmToken).await()
}