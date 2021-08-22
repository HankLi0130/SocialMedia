package app.hankdev.ui.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import app.hankdev.R
import app.hankdev.data.enums.AuthenticationState
import app.hankdev.ui.SharedViewModel
import app.hankdev.utils.ArchFragment
import app.hankdev.utils.Consts.REQUEST_SIGN_IN
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import tw.hankli.brookray.core.log.logInfo

class AuthFragment : ArchFragment<AuthViewModel>(R.layout.fragment_auth) {

    override val viewModel: AuthViewModel by viewModel()

    private val sharedViewModel by sharedViewModel<SharedViewModel>()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // If the user presses the back button, bring them back to the home screen.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.timelineFragment, false)
        }

        view_sign_in.setOnClickListener {
            startActivityForResult(viewModel.getSignInIntent(), REQUEST_SIGN_IN)
        }

        sharedViewModel.authenticationState.observe(viewLifecycleOwner) { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    logInfo("AUTHENTICATED from AuthFragment")
                }
                AuthenticationState.UNAUTHENTICATED -> {
                    logInfo("UNAUTHENTICATED from AuthFragment")
                }
                else -> {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            when {
                resultCode == RESULT_OK -> viewModel.onSignInSuccessfully(response)
                response == null -> viewModel.showMessage(messageRes = R.string.sign_in_canceled)
                else -> viewModel.onSignInFailed(response)
            }
        }
    }

    override fun notifyFromViewModel(code: Int) {
        when (code) {
            viewModel.signInSuccessfullyCode -> navController.navigateUp()
        }
    }
}
