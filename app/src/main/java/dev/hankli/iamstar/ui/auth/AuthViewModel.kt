package dev.hankli.iamstar.ui.auth

import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.IdpResponse
import dev.hankli.iamstar.repo.ProfileRepo
import dev.hankli.iamstar.utils.ArchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : ArchViewModel() {

    val profileCreatedCode = 1

    private val profileRepo = ProfileRepo()

    fun createProfile(response: IdpResponse) {
        callProgress(true)
        viewModelScope.launch(Dispatchers.IO) {
            profileRepo.createProfile(response)
            withContext(Dispatchers.Main) {
                callProgress(false)
                onProfileCreated()
            }
        }
    }

    fun onProfileCreated() = notifyView(profileCreatedCode)
}