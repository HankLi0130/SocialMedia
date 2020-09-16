package dev.hankli.iamstar.ui.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.firebase.ui.auth.IdpResponse
import dev.hankli.iamstar.R
import dev.hankli.iamstar.ui.MainActivity
import dev.hankli.iamstar.utils.BaseFragment
import dev.hankli.iamstar.utils.Consts.REQUEST_SIGN_IN
import dev.hankli.iamstar.utils.FirebaseUtil.getSignInIntent
import dev.hankli.iamstar.utils.showDialog
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : BaseFragment(R.layout.fragment_auth) {

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

            if (resultCode == RESULT_OK) {
                // Sign in successful.
                (requireActivity() as MainActivity).restart()
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
