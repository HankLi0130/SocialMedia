package tw.iamstar.repo

import android.content.Context
import tw.iamstar.data.models.Installation
import tw.iamstar.firebase.AuthManager
import tw.iamstar.firestore.InstallationManager
import tw.iamstar.firestore.ProfileManager
import tw.iamstar.utils.Consts.DEVICE_TYPE
import tw.iamstar.utils.SharedPreferencesManager
import java.util.*

class AuthRepo(
    private val installationManager: InstallationManager,
    private val profileManager: ProfileManager,
    private val spManager: SharedPreferencesManager
) {

    suspend fun createInstallation() {
        if (spManager.installationIdNotExists()) {
            val installation = Installation(deviceType = DEVICE_TYPE)
            installationManager.add(installation)
            spManager.saveInstallationId(installation.objectId)
        }
    }

    suspend fun updateInstallation(fcmToken: String) {
        val installationId = spManager.getInstallationId()!!
        val installation =
            installationManager.get(installationId, Installation::class.java)!!.apply {
                this.fcmToken = fcmToken
                this.profile = profileManager.getDoc(AuthManager.currentUserId!!)
                this.updatedAt = Date()
            }
        installationManager.set(installation)
    }

    suspend fun signOut(context: Context) {
        // remove FCM token
        if (spManager.installationIdExists()) {
            val installationId = spManager.getInstallationId()!!
            installationManager.removeFcmToken(installationId)
        }
        AuthManager.signOut(context)
    }
}