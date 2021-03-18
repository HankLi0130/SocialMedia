package tw.iamstar.ui.auth

import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.IdpResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.iamstar.R
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

    fun createProfile(response: IdpResponse) {
        viewModelScope.launch(Main) {
            callProgress(true)
            withContext(IO) { profileRepo.createProfile(response) }
            callProgress(false)
            onProfileCreated()
        }
    }

    fun onProfileCreated() = notifyView(profileCreatedCode)
}