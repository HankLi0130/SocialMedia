package app.hankdev.ui.auth

import androidx.lifecycle.viewModelScope
import app.hankdev.R
import app.hankdev.repo.AuthRepo
import app.hankdev.utils.ArchViewModel
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val authRepo: AuthRepo) : ArchViewModel() {

    val signInSuccessfullyCode = 1

    fun onSignInSuccessfully(response: IdpResponse?) {
        response?.let {
            viewModelScope.launch(Main) {
                callProgress(true)
                withContext(IO) { authRepo.signIn(it) }
                callProgress(false)
                notifyView(signInSuccessfullyCode)
            }
        } ?: showError(R.string.error_unknown)
    }

    fun onSignInFailed(response: IdpResponse) {
        response.error?.let { error ->
            error.printStackTrace()
            if (error.errorCode == ErrorCodes.NO_NETWORK) showNoInternet()
        } ?: showError(R.string.error_unknown)
    }

    fun getSignInIntent() = authRepo.getSignInIntent()
}