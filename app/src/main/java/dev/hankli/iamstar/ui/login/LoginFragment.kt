package dev.hankli.iamstar.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dev.hankli.iamstar.R
import dev.hankli.iamstar.databinding.FragmentLoginBinding
import dev.hankli.iamstar.ui.MainActivity
import dev.hankli.iamstar.utils.Consts.SIGN_IN_WITH_GOOGLE

class LoginFragment : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var hostActivity: MainActivity
    private val loginViewModel by viewModels<LoginViewModel>()
    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hostActivity = (activity as? MainActivity)!!
        binding = FragmentLoginBinding.inflate(inflater, container, false).apply {
            viewModel = loginViewModel
            lifecycleOwner = this@LoginFragment
        }
        setupListeners()
        setupObservers()
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    loginViewModel.handleFacebookLogin(result!!)
                }

                override fun onCancel() {
                    hostActivity.hideProgress()
                }

                override fun onError(error: FacebookException?) {
                    hostActivity.showDialog(error?.message!!)
                }
            })
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        return binding.root
    }

    private fun setupListeners() {
        binding.googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, SIGN_IN_WITH_GOOGLE)
        }
        binding.btLoginFacebook.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
        }
    }

    private fun setupObservers() {
        loginViewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                hostActivity.showProgress(it)
            } else {
                hostActivity.hideProgress()
            }
        })
        loginViewModel.alert.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                hostActivity.showDialog(it)
                loginViewModel.doneShowingAlert()
            }
        })
        loginViewModel.shouldNavigateToHome.observe(viewLifecycleOwner, Observer {
            if (it) {
                startActivity(Intent(context, MainActivity::class.java))
                hostActivity.finish()
                loginViewModel.doneNavigatingToHomeFragment()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        //google sign in
        if (requestCode == SIGN_IN_WITH_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                loginViewModel.handleGoogleLogin(account!!)
            } catch (e: ApiException) {
                e.printStackTrace()
                hostActivity.hideProgress()
                hostActivity.showDialog(getString(R.string.error_message))
            }
        }
    }
}