package dev.hankli.iamstar.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.Consts.REQUEST_PERMISSION_MEDIA
import dev.hankli.iamstar.utils.Consts.REQUEST_PICK_MEDIAS
import dev.hankli.iamstar.utils.ext.isInternetConnected
import dev.hankli.iamstar.utils.ext.toDateString
import dev.hankli.iamstar.utils.media.mediaPickerPermissions
import dev.hankli.iamstar.utils.media.obtainResult
import dev.hankli.iamstar.utils.media.showImagePicker
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EditProfileFragment :
    ArchFragment<EditProfileViewModel>(R.layout.fragment_edit_profile, R.menu.single_action_ok) {

    override val viewModel: EditProfileViewModel by viewModel()

    private val maxSelectable = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadProfile()

        viewModel.profileData.observe(viewLifecycleOwner) { profile ->
            profile.photoURL?.let {
                Glide.with(this).load(it).into(view_profile_head_shot)
            } ?: view_profile_head_shot.setImageResource(R.drawable.ic_person)

            view_input_profile_display_name.editText?.setText(profile.displayName)

            view_input_profile_description.editText?.setText(profile.description)

            view_input_profile_first_name.editText?.setText(profile.firstName)

            view_input_profile_last_name.editText?.setText(profile.lastName)

            view_input_profile_birthday.editText?.setText(profile.birthday?.toDateString())

            view_input_profile_email.editText?.setText(profile.email)

            view_input_profile_phone_number.editText?.setText(profile.phoneNumber)

            val gender = profile.gender?.let { getString(it.stringRes) }
            view_input_profile_gender.editText?.setText(gender)
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

        view_profile_head_shot.setOnClickListener {
            askingPermissions(mediaPickerPermissions, REQUEST_PERMISSION_MEDIA)
        }

        view_input_profile_birthday.editText?.setOnClickListener {
            val selection = viewModel.getBirthday()?.time ?: getDefaultSelection()

            val picker = MaterialDatePicker.Builder.datePicker()
                .setSelection(selection)
                .build()
                .apply {
                    addOnPositiveButtonClickListener {
                        viewModel.onBirthdayChanged(it)
                        this.clearOnPositiveButtonClickListeners()
                    }
                }
            picker.show(parentFragmentManager, null)
        }

        view_input_profile_gender.editText?.setOnClickListener {
            showListDialog(R.string.sex_types_title, R.array.sex_types) {
                viewModel.onSexChanged(it)
            }
        }
    }

    override fun onAllPermissionsGranted(requestCode: Int) {
        when (requestCode) {
            REQUEST_PERMISSION_MEDIA -> showImagePicker(this, maxSelectable, REQUEST_PICK_MEDIAS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_MEDIAS && resultCode == RESULT_OK) {
            val uris = obtainResult(data)
            if (uris.isNotEmpty()) {
                viewModel.onHeadShotSelected(uris[0])
            }
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

    // Default date is January 1 of 20 years ago.
    private fun getDefaultSelection(): Long {
        val cal = Calendar.getInstance().apply {
            clear(Calendar.HOUR)
            clear(Calendar.MINUTE)
            clear(Calendar.SECOND)
            clear(Calendar.MILLISECOND)
        }
        val defaultYear = cal.get(Calendar.YEAR) - 20
        cal.set(defaultYear, Calendar.JANUARY, 1)
        return cal.timeInMillis
    }
}