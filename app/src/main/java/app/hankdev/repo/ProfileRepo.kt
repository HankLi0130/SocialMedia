package app.hankdev.repo

import android.net.Uri
import app.hankdev.data.models.firestore.Profile
import app.hankdev.firebase.AuthManager
import app.hankdev.firebase.BUCKET_PROFILE
import app.hankdev.firebase.StorageManager
import app.hankdev.firestore.ProfileManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

class ProfileRepo(
    private val profileManager: ProfileManager,
    private val authManager: AuthManager,
    private val storageManager: StorageManager
) {

    suspend fun getProfile(userId: String): Profile? = profileManager.get(userId)

    suspend fun updateProfile(profile: Profile) = profileManager.update(profile)

    suspend fun getPhotoURL(userId: String) = profileManager.getPhotoUrl(userId)


    suspend fun updateHeadshot(uri: Uri): String {
        val userId = authManager.currentUserId!!
        val filePath = "${BUCKET_PROFILE}/${userId}"
        val url = storageManager.uploadFile(filePath, uri)
        profileManager.updatePhotoUrl(userId, url)
        return url
    }

    fun observeProfile(
        userId: String,
        listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit
    ) = profileManager.observeDoc(userId, listener)
}