package dev.hankli.iamstar.ui.profile

import android.os.Bundle
import android.view.View
import dev.hankli.iamstar.R
import dev.hankli.iamstar.ui.MainActivity
import dev.hankli.iamstar.utils.BaseFragment
import dev.hankli.iamstar.utils.FirebaseUtil.auth
import dev.hankli.iamstar.utils.FirebaseUtil.signOut
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth.currentUser?.let {
            view_user_info.text = "user id: ${it.uid}"
        }

        view_sign_out.setOnClickListener {
            signOut(requireContext()) {
                (requireActivity() as MainActivity).restart()
            }
        }
    }
}