package dev.hankli.iamstar.ui.auth

import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.IdpResponse
import dev.hankli.iamstar.repo.ProfileRepo
import dev.hankli.iamstar.utils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : BaseViewModel() {

    val profileCreatedCode = 1

    private val profileRepo = ProfileRepo()

    // Provider Type https://firebase.google.com/docs/projects/provisioning/configure-oauth#add-idp
    fun createProfile(response: IdpResponse) {
        callProgress(true)
        viewModelScope.launch(Dispatchers.IO) {
            profileRepo.add()
            withContext(Dispatchers.Main) {
                callProgress(false)
                onProfileCreated()
            }
        }
    }

    fun onProfileCreated() = notifyView(profileCreatedCode)
}