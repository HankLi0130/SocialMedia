package dev.hankli.iamstar.repo

import android.net.Uri
import com.firebase.ui.auth.IdpResponse
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.firebase.AuthManager
import dev.hankli.iamstar.firebase.StorageManager
import dev.hankli.iamstar.firestore.ProfileManager

class ProfileRepo {

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
        ProfileManager.update(profile)
    }

    suspend fun updateHeadshot(userId: String, uri: Uri): String {
        val filePath = "${BUCKET_PROFILE}/${userId}"
        val url = StorageManager.uploadFile2(filePath, uri)
        ProfileManager.updateHeadshot(userId, url)
        return url
    }
}