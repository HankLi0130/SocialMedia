package tw.iamstar.ui.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import tw.iamstar.R
import tw.iamstar.firebase.AuthManager.getSignInIntent
import tw.iamstar.utils.ArchFragment
import tw.iamstar.utils.Consts.REQUEST_SIGN_IN
import tw.iamstar.utils.ext.isInternetConnected

class AuthFragment : ArchFragment<AuthViewModel>(R.layout.fragment_auth) {

    override val viewModel: AuthViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (requireContext().isInternetConnected()) {
            viewModel.createInstallation()
        } else {
            viewModel.showNoInternet()
        }

        view_sign_in.setOnClickListener {
            startActivityForResult(getSignInIntent(), REQUEST_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            when {
                resultCode == RESULT_OK -> onSignInSuccessfully(response)
                response == null -> onSignInCanceled()
                else -> onSignInFailed(response)
            }
        }
    }

    override fun notifyFromViewModel(code: Int) {
        when (code) {
            viewModel.profileCreatedCode -> findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToNavMain())
            viewModel.installationCreatedCode -> view_sign_in.isEnabled = true
        }
    }

    private fun onSignInSuccessfully(response: IdpResponse?) {
        response?.let {
            if (it.isNewUser) viewModel.createProfile(it) else viewModel.onProfileCreated()
        } ?: viewModel.showError(R.string.error_unknown)
    }

    private fun onSignInCanceled() {
        viewModel.showMessage(messageRes = R.string.sign_in_canceled)
    }

    private fun onSignInFailed(response: IdpResponse) {
        response.error?.let { error ->
            error.printStackTrace()
            if (error.errorCode == ErrorCodes.NO_NETWORK) viewModel.showNoInternet()
        } ?: viewModel.showError(R.string.error_unknown)
    }
}
