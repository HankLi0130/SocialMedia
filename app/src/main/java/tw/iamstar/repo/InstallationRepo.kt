package tw.iamstar.repo

import tw.iamstar.data.models.Installation
import tw.iamstar.firestore.InstallationManager
import tw.iamstar.utils.Consts.DEVICE_TYPE
import tw.iamstar.utils.SharedPreferencesManager

class InstallationRepo(
    private val installationManager: InstallationManager,
    private val spManager: SharedPreferencesManager
) {

    suspend fun createInstallation() {
        if (spManager.installationIdNotExists()) {
            val installation = Installation(deviceType = DEVICE_TYPE)
            installationManager.add(installation)
            spManager.saveInstallationId(installation.objectId)
        }
    }

}