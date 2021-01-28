package dev.hankli.iamstar.ui.schedule

import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScheduleFragment: ArchFragment<ScheduleViewModel>(R.layout.fragment_schedule) {
    override val viewModel: ScheduleViewModel by viewModel()
}