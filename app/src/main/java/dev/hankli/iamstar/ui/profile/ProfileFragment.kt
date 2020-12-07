package dev.hankli.iamstar.ui.profile

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import dev.hankli.iamstar.firebase.AuthManager
import dev.hankli.iamstar.utils.BaseArchFragment
import dev.hankli.iamstar.utils.ext.display
import kotlinx.android.synthetic.main.card_field.view.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseArchFragment<ProfileViewModel>(R.layout.fragment_profile) {

    override val hasOptionsMenu: Boolean
        get() = true

    override val menuRes: Int
        get() = R.menu.fragment_profile

    override val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.subscribeProfile(app.user.id)

        viewModel.profileData.observe(viewLifecycleOwner) { profile ->

            profile.photoURL?.let {
                Glide.with(this).load(it).into(view_profile_head_shot)
            } ?: run { view_profile_head_shot.setImageResource(R.drawable.ic_person) }

            profile.displayName?.let {
                view_display_name.isVisible = true
                view_display_name.view_title.text = getString(R.string.profile_display_name)
                view_display_name.view_field.text = it
            } ?: run { view_display_name.isVisible = false }

            profile.description?.let {
                view_description.isVisible = true
                view_description.view_title.text = getString(R.string.profile_description)
                view_description.view_field.text = it
            } ?: run { view_description.isVisible = false }

            profile.firstName?.let {
                view_first_name.isVisible = true
                view_first_name.view_title.text = getString(R.string.profile_first_name)
                view_first_name.view_field.text = it
            } ?: run { view_first_name.isVisible = false }

            profile.lastName?.let {
                view_last_name.isVisible = true
                view_last_name.view_title.text = getString(R.string.profile_last_name)
                view_last_name.view_field.text = it
            } ?: run { view_last_name.isVisible = false }

            profile.birthday?.let {
                view_birthday.isVisible = true
                view_birthday.view_title.text = getString(R.string.profile_birthday)
                view_birthday.view_field.text = it.display()
            } ?: run { view_birthday.isVisible = false }

            profile.email?.let {
                view_email.isVisible = true
                view_email.view_title.text = getString(R.string.profile_email)
                view_email.view_field.text = it
            } ?: run { view_email.isVisible = false }

            profile.phoneNumber?.let {
                view_phone_number.isVisible = true
                view_phone_number.view_title.text = getString(R.string.profile_phone_number)
                view_phone_number.view_field.text = it
            } ?: run { view_phone_number.isVisible = false }

            profile.sex?.let {
                view_sex.isVisible = true
                view_sex.view_title.text = getString(R.string.profile_sex)
                view_sex.view_field.text = it
            } ?: run { view_sex.isVisible = false }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update_profile -> updateProfile()
            R.id.action_sign_out -> signOut()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateProfile(): Boolean {
        findNavController()
            .navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
        return true
    }

    private fun signOut(): Boolean {
        AuthManager.signOut(requireContext()) { mainActivity.restart() }
        return true
    }
}