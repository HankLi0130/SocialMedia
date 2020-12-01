package dev.hankli.iamstar.ui.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import dev.hankli.iamstar.R
import dev.hankli.iamstar.firebase.AuthManager.getSignInIntent
import dev.hankli.iamstar.utils.BaseArchFragment
import dev.hankli.iamstar.utils.Consts.REQUEST_SIGN_IN
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : BaseArchFragment<AuthViewModel>(R.layout.fragment_auth) {

    override val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_sign_in.setOnClickListener {
            startActivityForResult(getSignInIntent(), REQUEST_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            when {
                resultCode == RESULT_OK -> onSignInSuccessfully()
                response == null -> onSignInCanceled()
                response.error?.errorCode == ErrorCodes.NO_NETWORK -> onSignInNoNetwork()
                else -> onSignInFailed(response)
            }
        }
    }

    private fun onSignInSuccessfully() {
        app.loadUser()
        findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToFeedFragment())
    }

    private fun onSignInCanceled() {
        viewModel.showMessage(messageRes = R.string.sign_in_canceled)
    }

    private fun onSignInNoNetwork() {
        viewModel.showNoInternet()
    }

    private fun onSignInFailed(response: IdpResponse) {
        response.error?.printStackTrace()
        viewModel.showError(R.string.error_unknown)
    }
}
