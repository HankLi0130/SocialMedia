package dev.hankli.iamstar.ui.schedule

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import dev.hankli.iamstar.R
import dev.hankli.iamstar.utils.ArchFragment
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScheduleFragment : ArchFragment<ScheduleViewModel>(R.layout.fragment_schedule) {
    override val viewModel: ScheduleViewModel by viewModel()

    private val scheduleAdapter = ScheduleAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_schedule_list.apply {
            setHasFixedSize(true)
            adapter = scheduleAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        }

        scheduleAdapter.item = viewModel.getSchedules()
        scheduleAdapter.notifyDataSetChanged()
    }
}