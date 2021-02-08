package dev.hankli.iamstar.ui.schedule

import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditScheduleFragment : ArchFragment<EditScheduleViewModel>(R.layout.fragment_edit_schedule) {
    override val viewModel: EditScheduleViewModel by viewModel()
}