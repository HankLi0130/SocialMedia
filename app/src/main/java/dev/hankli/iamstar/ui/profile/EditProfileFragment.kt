package dev.hankli.iamstar.ui.profile

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.BaseArchFragment
import dev.hankli.iamstar.utils.ext.isInternetConnected
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment : BaseArchFragment<EditProfileViewModel>(R.layout.fragment_edit_profile) {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.single_action_ok

    override val viewModel: EditProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadProfile(app.user.id)

        viewModel.profileData.observe(viewLifecycleOwner) { profile ->
            profile.photoURL?.let {
                Glide.with(this).load(it).into(view_profile_head_shot)
            } ?: view_profile_head_shot.setImageResource(R.drawable.ic_person)

            view_input_profile_display_name.editText?.setText(profile.displayName)

            view_input_profile_description.editText?.setText(profile.description)

            view_input_profile_first_name.editText?.setText(profile.firstName)

            view_input_profile_last_name.editText?.setText(profile.lastName)

            view_input_profile_birthday.editText?.setText(profile.birthday.toString())

            view_input_profile_email.editText?.setText(profile.email)

            view_input_profile_phone_number.editText?.setText(profile.phoneNumber)

            view_input_profile_sex.editText?.setText(profile.sex)
        }

        view_input_profile_display_name.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onDisplayNameChanged(text)
        }

        view_input_profile_description.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionChanged(text)
        }

        view_input_profile_first_name.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onFirstNameChanged(text)
        }

        view_input_profile_last_name.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onLastNameChanged(text)
        }

        view_input_profile_email.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEmailChanged(text)
        }

        view_input_profile_phone_number.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onPhoneNumberChanged(text)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ok -> submit()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun submit(): Boolean {
        if (requireContext().isInternetConnected()) {
            viewModel.submit()
        } else viewModel.showNoInternet()
        return true
    }
}