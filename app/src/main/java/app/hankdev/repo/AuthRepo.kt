package app.hankdev.repo

import android.content.Context
import app.hankdev.data.models.firestore.Profile
import app.hankdev.firebase.AuthManager
import app.hankdev.firebase.MessagingManager
import app.hankdev.firestore.InstallationManager
import app.hankdev.firestore.ProfileManager
import app.hankdev.utils.Consts.DEVICE_TYPE
import app.hankdev.utils.SharedPreferencesManager
import com.firebase.ui.auth.IdpResponse

class AuthRepo(
    private val installationManager: InstallationManager,
    private val profileManager: ProfileManager,
    private val spManager: SharedPreferencesManager,
    private val authManager: AuthManager,
    private val messagingManager: MessagingManager
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
        val user = authManager.currentUser!!

        val profile = Profile(
            id = user.uid,
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
            fcmToken = messagingManager.getToken(),
            profile = profileManager.getDoc(authManager.currentUserId!!),
            deviceType = DEVICE_TYPE
        )
        spManager.saveInstallationId(installationId)
    }

    private suspend fun updateInstallation() {
        val installationId = spManager.getInstallationId()!!
        installationManager.update(
            installationId,
            messagingManager.getToken(),
            profileManager.getDoc(authManager.currentUserId!!),
            DEVICE_TYPE
        )
    }

    private suspend fun subscribeToTopic() {
        val topic = "topic"
        messagingManager.subscribeToTopic(topic)
    }

    suspend fun signOut(context: Context) {
        // remove FCM token
        if (spManager.installationIdExists()) {
            val installationId = spManager.getInstallationId()!!
            installationManager.removeFcmToken(installationId)
        }
        val topic = "topic"
        messagingManager.unsubscribeFromTopic(topic)
        authManager.signOut(context)
    }

    fun getSignInIntent() = authManager.getSignInIntent()
}