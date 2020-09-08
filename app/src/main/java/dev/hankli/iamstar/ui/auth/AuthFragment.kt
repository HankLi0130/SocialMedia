package dev.hankli.iamstar.ui.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import dev.hankli.iamstar.R
import dev.hankli.iamstar.ui.MainActivity
import dev.hankli.iamstar.utils.Consts.SIGN_IN
import dev.hankli.iamstar.utils.showDialog
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : Fragment(R.layout.fragment_auth) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_sign_in.setOnClickListener { loginWithFirebase() }
    }

    private fun loginWithFirebase() {
        val provider = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("tw").build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .build(),
            SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Sign in successful.
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

                if (response != null) {
                    val message = StringBuilder(getString(R.string.error_message))
                    response.error?.let {
                        it.printStackTrace()
                        message.append(" ${it.message}")
                    }
                    showDialog(requireContext(), message.toString())
                }
            }
        }
    }
}
