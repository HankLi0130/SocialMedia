package tw.iamstar.repo

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import tw.iamstar.data.models.Profile
import tw.iamstar.firebase.AuthManager
import tw.iamstar.firebase.BUCKET_PROFILE
import tw.iamstar.firebase.StorageManager
import tw.iamstar.firestore.ProfileManager

class ProfileRepo(private val profileManager: ProfileManager) {

    suspend fun getProfile(userId: String): Profile? = profileManager.get(userId)

    suspend fun updateProfile(profile: Profile) = profileManager.update(profile)

    suspend fun getPhotoURL(userId: String) = profileManager.getPhotoUrl(userId)


    suspend fun updateHeadshot(uri: Uri): String {
        val userId = AuthManager.currentUserId!!
        val filePath = "${BUCKET_PROFILE}/${userId}"
        val url = StorageManager.uploadFile(filePath, uri)
        profileManager.updatePhotoUrl(userId, url)
        return url
    }

    fun observeProfile(
        userId: String,
        listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit
    ) = profileManager.observeDoc(userId, listener)
}