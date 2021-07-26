package app.hankdev.firestore

import app.hankdev.data.models.firestore.Profile
import com.google.firebase.firestore.CollectionReference
import java.util.*

class ProfileManager(collection: CollectionReference) : FirestoreManager<Profile>(collection) {

    suspend fun get(userId: String): Profile? = get(userId, Profile::class.java)

    suspend fun update(profile: Profile) {
        profile.updatedAt = Date()
        set(profile)
    }

    suspend fun getPhotoUrl(userId: String) = get(userId)?.photoURL

    suspend fun updatePhotoUrl(userId: String, url: String) {
        update(userId, Profile.PHOTO_URL to url)
    }

}