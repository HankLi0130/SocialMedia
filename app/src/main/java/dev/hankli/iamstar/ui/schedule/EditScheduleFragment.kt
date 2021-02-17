package dev.hankli.iamstar.ui.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.Consts
import dev.hankli.iamstar.utils.ext.isInternetConnected
import dev.hankli.iamstar.utils.ext.toDateString
import dev.hankli.iamstar.utils.ext.toTimeString
import dev.hankli.iamstar.utils.getPlacesIntent
import dev.hankli.iamstar.utils.media.*
import kotlinx.android.synthetic.main.card_schedule.*
import kotlinx.android.synthetic.main.fragment_edit_schedule.*
import kotlinx.android.synthetic.main.fragment_edit_schedule.view_schedule_location
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditScheduleFragment :
    ArchFragment<EditScheduleViewModel>(R.layout.fragment_edit_schedule, R.menu.single_action_ok) {
    override val viewModel: EditScheduleViewModel by viewModel()

    private val argus: EditScheduleFragmentArgs by navArgs()

    private val maxImageSelectable = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadSchedule(argus.scheduleId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.getPhotoURL(app.influencerId)?.let { url ->
                Glide.with(this@EditScheduleFragment).load(url).into(view_schedule_head_shot)
            } ?: view_schedule_head_shot.setImageResource(R.drawable.ic_person)
        }

        viewModel.locationData.observe(viewLifecycleOwner, { location ->
            view_schedule_location.text = location
        })

        view_input_schedule_title.editText?.let {
            it.doOnTextChanged { text, _, _, _ ->
                viewModel.onTitleChange(text)
            }
        }

        viewModel.titleData.observe(viewLifecycleOwner) { title ->
            view_input_schedule_title.editText?.setText(title)
        }

        viewModel.startDateTime.observe(viewLifecycleOwner) { startDateTime ->
            view_input_start_date.text = startDateTime.toDateString()
            view_input_start_time.text = startDateTime.toTimeString()
        }

        view_input_start_date.setOnClickListener {
            showDatePicker(R.string.start_date_title, viewModel.getStartDateInMillis()) { date ->
                viewModel.setStartDateInMillis(date)
            }
        }
        view_input_start_time.setOnClickListener {
            showTimePicker(
                R.string.start_time_title,
                viewModel.getStartHour(),
                viewModel.getStartMinute()
            ) { hour, minute ->
                viewModel.setStartTime(hour, minute)
            }
        }

        viewModel.endDateTime.observe(viewLifecycleOwner) { endDateTime ->
            view_input_end_date.text = endDateTime.toDateString()
            view_input_end_time.text = endDateTime.toTimeString()
        }
        view_input_end_date.setOnClickListener {
            showDatePicker(R.string.end_date_title, viewModel.getEndDateInMillis()) { date ->
                viewModel.setEndDateInMillis(date)
            }
        }
        view_input_end_time.setOnClickListener {
            showTimePicker(
                R.string.end_time_title,
                viewModel.getEndHour(),
                viewModel.getEndMinute()
            ) { hour, minute ->
                viewModel.setEndTime(hour, minute)
            }
        }

        view_add_location.setOnClickListener {
            startActivityForResult(getPlacesIntent(requireContext()), Consts.REQUEST_PLACES)
        }

        view_add_photos.setOnClickListener {
            askingPermissions(mediaPickerPermissions, Consts.REQUEST_PERMISSION_MEDIA)
        }

        view_cancel.setOnClickListener {
            viewModel.removeImage()
        }

        viewModel.imageData.observe(viewLifecycleOwner) { image ->
            Glide.with(this).load(image?.uri).into(view_schedule_image)
            view_cancel.isVisible = image != null
        }
    }

    private fun showDatePicker(
        titleId: Int,
        selection: Long,
        listener: MaterialPickerOnPositiveButtonClickListener<Long>
    ) {
        MaterialDatePicker.Builder
            .datePicker()
            .setSelection(selection)
            .setTitleText(titleId)
            .build()
            .apply {
                addOnPositiveButtonClickListener(listener)
            }
            .show(parentFragmentManager, null)
    }

    private fun showTimePicker(
        titleId: Int,
        hour: Int,
        minute: Int,
        listener: (hour: Int, minute: Int) -> Unit
    ) {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText(titleId)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    listener(this.hour, this.minute)
                }
            }
            .show(parentFragmentManager, null)
    }

    override fun onAllPermissionsGranted(requestCode: Int) {
        when (requestCode) {
            Consts.REQUEST_PERMISSION_MEDIA -> selectImage()
        }
    }

    private fun selectImage() {
        if (viewModel.isImageSelected()) {
            showMessageDialog(
                getString(R.string.error_title),
                getString(R.string.up_to_image_maximum, maxImageSelectable)
            )
        } else {
            showImagePicker(
                this,
                maxImageSelectable,
                Consts.REQUEST_PICK_MEDIAS
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Consts.REQUEST_PICK_MEDIAS -> handleMedias(resultCode, data)
            Consts.REQUEST_PLACES -> handlePlaces(resultCode, data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleMedias(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val uris = obtainResult(data)
            val paths = obtainPathResult(data)
            val selectedMediaFiles = toMediaFiles(
                requireContext().contentResolver,
                uris,
                paths
            )

            viewModel.addImage(selectedMediaFiles[0])
        }
    }

    private fun handlePlaces(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.let {
                    Autocomplete.getPlaceFromIntent(it).run {
                        viewModel.setLocation(
                            name,
                            latLng?.latitude,
                            latLng?.longitude
                        )
                    }
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                // TODO handle error
            }
            Activity.RESULT_CANCELED -> {
                // TODO The user canceled the operation.
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ok -> {
                if (requireContext().isInternetConnected()) {
                    viewModel.submit(requireContext().contentResolver, app.influencerId)
                } else viewModel.showNoInternet()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}