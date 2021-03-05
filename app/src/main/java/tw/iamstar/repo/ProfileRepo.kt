package tw.iamstar.repo

import android.net.Uri
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import tw.iamstar.data.models.Profile
import tw.iamstar.firebase.AuthManager
import tw.iamstar.firebase.BUCKET_PROFILE
import tw.iamstar.firebase.StorageManager
import tw.iamstar.firestore.ProfileManager
import java.util.*

class ProfileRepo(private val profileManager: ProfileManager) {

    // https://firebase.google.com/docs/projects/provisioning/configure-oauth#add-idp
    suspend fun createProfile(response: IdpResponse) {
        val loginMethod = when (response.providerType) {
            "facebook.com" -> "FACEBOOK"
            "google.com" -> "GOOGLE"
            "phone" -> "PHONE"
            "password" -> "EMAIL"
            else -> null
        }
        val user = AuthManager.currentUser!!

        val profile = Profile(
            objectId = user.uid,
            displayName = user.displayName,
            email = user.email,
            phoneNumber = user.phoneNumber,
            photoURL = user.photoUrl?.toString(),
            loginMethod = loginMethod
        )
        profileManager.set(profile)
    }

    suspend fun getProfile(userId: String): Profile? {
        return profileManager.get(userId, Profile::class.java)
    }

    suspend fun updateProfile(profile: Profile) {
        profile.updatedAt = Date()
        profileManager.set(profile)
    }

    suspend fun getPhotoURL(userId: String): String? {
        return profileManager.getPhotoURL(userId)
    }

    suspend fun updateHeadshot(uri: Uri): String {
        val userId = AuthManager.currentUserId!!
        val filePath = "${BUCKET_PROFILE}/${userId}"
        val url = StorageManager.uploadFile(filePath, uri)
        profileManager.updateHeadshot(userId, url)
        return url
    }

    fun observeProfile(
        userId: String,
        listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit
    ) = profileManager.observeDoc(userId, listener)

    suspend fun updateFcmToken(userId: String, fcmToken: String) =
        profileManager.updateFcmToken(userId, fcmToken)
}