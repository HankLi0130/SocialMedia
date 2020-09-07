package dev.hankli.iamstar.ui.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import dev.hankli.iamstar.R
import dev.hankli.iamstar.databinding.FragmentAuthBinding
import dev.hankli.iamstar.ui.MainActivity
import dev.hankli.iamstar.utils.Consts.SIGN_IN
import dev.hankli.iamstar.utils.FirestoreUtil.auth

class AuthFragment : Fragment() {

    private lateinit var hostActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.btnLogin.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.authFragment) {
//                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToLoginFragment())
                loginWithFirebase()
            }
        }
        binding.btnSignup.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.authFragment) {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToSignUpFragment())
            }
        }

        hostActivity = activity as MainActivity

        return binding.root
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
                val user = auth.currentUser!!

                Log.i("test", user.toString())

//                getUserByID(user.uid, OnCompleteListener {
//                    if (it.isSuccessful) {
//                        val document = it.result
//                        if (document!!["email"] != null) {
//                            val userInfo = document.toObject(UserModel::class.java)
//                            App.currentUser = userInfo
//                            startActivity(Intent(context, MainActivity::class.java))
//                            hostActivity.finish()
//                        }
//                    }
//                })
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                val errorMessage = StringBuilder(getString(R.string.error_message))
                response?.let {
                    val errorCode = it.error?.errorCode
                    errorMessage.append(" Error Code: $errorCode")
                }
                hostActivity.showDialog(errorMessage.toString())
            }
        }
    }
}
