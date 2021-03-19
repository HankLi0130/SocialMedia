package tw.iamstar.ui.auth

import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.iamstar.R
import tw.iamstar.repo.AuthRepo
import tw.iamstar.repo.ProfileRepo
import tw.iamstar.utils.ArchViewModel

class AuthViewModel(
    private val profileRepo: ProfileRepo,
    private val authRepo: AuthRepo
) : ArchViewModel() {

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
}