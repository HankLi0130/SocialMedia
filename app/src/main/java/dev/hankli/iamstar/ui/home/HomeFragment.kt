package dev.hankli.iamstar.ui.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.FirebaseUtil.auth
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view_user_info.setOnClickListener { view ->
            auth.currentUser?.let {
                (view as TextView).text = "user id: ${it.uid}"
            }
        }
    }
}