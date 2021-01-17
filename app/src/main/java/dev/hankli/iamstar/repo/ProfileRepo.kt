package dev.hankli.iamstar.repo

import android.net.Uri
import com.firebase.ui.auth.IdpResponse
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.firebase.AuthManager
import dev.hankli.iamstar.firebase.StorageManager
import dev.hankli.iamstar.firestore.ProfileManager
import java.util.*

class ProfileRepo(private val profileManager: ProfileManager) {

    companion object {
        private const val BUCKET_PROFILE = "Profile"
    }

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

    suspend fun fetchProfile(userId: String): Profile? {
        return profileManager.get(userId, Profile::class.java)
    }

    suspend fun updateProfile(profile: Profile) {
        profile.updatedAt = Date()
        profileManager.set(profile)
    }

    suspend fun updateHeadshot(uri: Uri): String {
        val userId = AuthManager.currentUserId!!
        val filePath = "${BUCKET_PROFILE}/${userId}"
        val url = StorageManager.uploadFile(filePath, uri)
        profileManager.updateHeadshot(userId, url)
        return url
    }
}