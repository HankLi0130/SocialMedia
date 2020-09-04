package dev.hankli.iamstar.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.hankli.iamstar.R
import dev.hankli.iamstar.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.btnLogin.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.authFragment) {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToLoginFragment())
            }
        }
        binding.btnSignup.setOnClickListener {
//            if (findNavController().currentDestination?.id == R.id.authFragment) {
//                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToSignUpFragment())
//            }
        }
        return binding.root
    }
}
