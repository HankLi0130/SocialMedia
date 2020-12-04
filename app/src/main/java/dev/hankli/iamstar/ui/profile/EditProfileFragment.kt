package dev.hankli.iamstar.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseArchFragment

class EditProfileFragment : BaseArchFragment<EditProfileViewModel>(R.layout.fragment_edit_profile) {
    override val viewModel: EditProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}