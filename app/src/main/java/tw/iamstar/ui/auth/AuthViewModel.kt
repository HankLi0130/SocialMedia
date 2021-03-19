package tw.iamstar.ui.auth

import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.iamstar.R
import tw.iamstar.firebase.MessagingManager
import tw.iamstar.repo.InstallationRepo
import tw.iamstar.repo.ProfileRepo
import tw.iamstar.utils.ArchViewModel

class AuthViewModel(
    private val profileRepo: ProfileRepo,
    private val installationRepo: InstallationRepo
) : ArchViewModel() {

    val profileCreatedCode = 1
    val installationCreatedCode = 2

    fun createInstallation() {
        viewModelScope.launch(Main) {
            callProgress(true)
            try {
                withContext(IO) { installationRepo.createInstallation() }
            } catch (e: Throwable) {
                callProgress(false)
                e.printStackTrace()
                showError(R.string.error_system)
            }
            callProgress(false)
            notifyView(installationCreatedCode)
        }
    }

    private suspend fun updateInstallation() {
        val fcmToken = MessagingManager.getToken()
        installationRepo.updateInstallation(fcmToken)
    }

    private suspend fun createProfile(response: IdpResponse) = profileRepo.createProfile(response)

    private fun onProfileCreated() = notifyView(profileCreatedCode)

    fun onSignInSuccessfully(response: IdpResponse?) {
        response?.let {
            viewModelScope.launch(Main) {
                callProgress(true)
                withContext(IO) {
                    if (it.isNewUser) createProfile(it)
                }
                withContext(IO) {
                    updateInstallation()
                }
                callProgress(false)
                onProfileCreated()
            }
        } ?: showError(R.string.error_unknown)
    }

    fun onSignInFailed(response: IdpResponse) {
        response.error?.let { error ->
            error.printStackTrace()
            if (error.errorCode == ErrorCodes.NO_NETWORK) showNoInternet()
        } ?: showError(R.string.error_unknown)
    }
}