package dev.hankli.iamstar.ui.profile

import androidx.fragment.app.viewModels
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseArchFragment

class EditProfileFragment : BaseArchFragment<EditProfileViewModel>(R.layout.fragment_edit_profile) {
    override val viewModel: EditProfileViewModel by viewModels()
}