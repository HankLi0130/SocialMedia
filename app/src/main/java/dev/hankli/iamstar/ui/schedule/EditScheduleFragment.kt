package dev.hankli.iamstar.ui.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.navArgs
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import dev.hankli.iamstar.utils.Consts
import dev.hankli.iamstar.utils.ext.toDateString
import dev.hankli.iamstar.utils.ext.toTimeString
import dev.hankli.iamstar.utils.getPlacesIntent
import kotlinx.android.synthetic.main.fragment_edit_schedule.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditScheduleFragment : ArchFragment<EditScheduleViewModel>(R.layout.fragment_edit_schedule) {
    override val viewModel: EditScheduleViewModel by viewModel()

    private val argus: EditScheduleFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadSchedule(argus.scheduleId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.locationData.observe(viewLifecycleOwner, { location ->
            view_schedule_location.text = location
        })

        view_input_schedule_title.editText?.let {
            it.doOnTextChanged { text, _, _, _ ->
                viewModel.onTitleChange(text)
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Consts.REQUEST_PLACES -> handlePlaces(resultCode, data)
            else -> super.onActivityResult(requestCode, resultCode, data)
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
}