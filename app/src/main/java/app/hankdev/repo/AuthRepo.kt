package app.hankdev.repo

import android.content.Context
import app.hankdev.BuildConfig
import app.hankdev.data.models.firestore.Profile
import app.hankdev.firebase.AuthManager
import app.hankdev.firebase.MessagingManager
import app.hankdev.firestore.ApplicationManager
import app.hankdev.firestore.InstallationManager
import app.hankdev.firestore.ProfileManager
import app.hankdev.utils.Consts.DEVICE_TYPE
import app.hankdev.utils.SharedPreferencesManager
import com.firebase.ui.auth.IdpResponse

class AuthRepo(
    private val applicationManager: ApplicationManager,
    private val installationManager: InstallationManager,
    private val profileManager: ProfileManager,
    private val spManager: SharedPreferencesManager
) {

    suspend fun signIn(response: IdpResponse) {
        if (response.isNewUser) createProfile(response)
        if (spManager.installationIdExists()) updateInstallation() else createInstallation()
        subscribeToTopic()
    }

    private suspend fun createProfile(response: IdpResponse) {
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

    private suspend fun createInstallation() {
        val installationId = installationManager.add(
            fcmToken = MessagingManager.getToken(),
            profile = profileManager.getDoc(AuthManager.currentUserId!!),
            deviceType = DEVICE_TYPE
        )
        spManager.saveInstallationId(installationId)
    }

    private suspend fun updateInstallation() {
        val installationId = spManager.getInstallationId()!!
        installationManager.update(
            installationId,
            MessagingManager.getToken(),
            profileManager.getDoc(AuthManager.currentUserId!!),
            DEVICE_TYPE
        )
    }

    private suspend fun subscribeToTopic() {
        val topic = BuildConfig.APPLICATION_ID
        MessagingManager.subscribeToTopic(topic)
    }

    suspend fun signOut(context: Context) {
        // remove FCM token
        if (spManager.installationIdExists()) {
            val installationId = spManager.getInstallationId()!!
            installationManager.removeFcmToken(installationId)
        }
        val topic = BuildConfig.APPLICATION_ID
        MessagingManager.unsubscribeFromTopic(topic)
        AuthManager.signOut(context)
    }
}