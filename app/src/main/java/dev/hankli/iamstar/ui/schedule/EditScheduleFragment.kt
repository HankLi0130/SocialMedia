package dev.hankli.iamstar.ui.schedule

import android.os.Bundle
import android.view.View
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import kotlinx.android.synthetic.main.fragment_edit_schedule.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditScheduleFragment : ArchFragment<EditScheduleViewModel>(R.layout.fragment_edit_schedule) {
    override val viewModel: EditScheduleViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_input_start_date.setOnClickListener { showDatePicker() }
        view_input_start_time.setOnClickListener { showTimePicker() }
        view_input_end_date.setOnClickListener { showDatePicker() }
        view_input_end_time.setOnClickListener { showTimePicker() }
    }

    private fun showDatePicker() {
        MaterialDatePicker.Builder.datePicker()
            .build()
            .showNow(parentFragmentManager, null)
    }

    private fun showTimePicker() {
        MaterialTimePicker.Builder()
            .build()
            .showNow(parentFragmentManager, null)
    }
}