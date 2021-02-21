package dev.hankli.iamstar.ui.schedule

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import dev.hankli.iamstar.R
import dev.hankli.iamstar.firebase.AuthManager
import dev.hankli.iamstar.utils.ArchFragment
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import tw.hankli.brookray.core.constant.EMPTY

class ScheduleFragment :
    ArchFragment<ScheduleViewModel>(R.layout.fragment_schedule, R.menu.fragment_schedule) {
    override val viewModel: ScheduleViewModel by viewModel()

    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Role Permission
        val writable = AuthManager.currentUserId == app.influencerId
        setMenuVisibility(writable)

        scheduleAdapter = ScheduleAdapter(viewModel.getScheduleOptions(app.influencerId)).apply {
            if (writable) onItemLongClick = ::onScheduleLongClick
            onItemClick = ::onScheduleClick
        }

        view_schedule_list.apply {
            setHasFixedSize(true)
            adapter = scheduleAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        }
    }

    override fun notifyFromViewModel(code: Int) {
        when (code) {
            viewModel.refreshSchedulesCode -> scheduleAdapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_schedule -> {
                toEditScheduleFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toEditScheduleFragment(scheduleId: String = EMPTY) {
        findNavController().navigate(
            ScheduleFragmentDirections.actionScheduleFragmentToEditScheduleFragment(scheduleId)
        )
    }

    override fun onStart() {
        super.onStart()
        scheduleAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        scheduleAdapter.stopListening()
    }

    private fun onScheduleLongClick(scheduleId: String): Boolean {
        showListDialog(R.string.schedule_actions_title, R.array.schedule_actions) { which ->
            when (which) {
                0 -> toEditScheduleFragment(scheduleId)
                1 -> viewModel.deleteSchedule(scheduleId)
            }
        }
        return true
    }

    private fun onScheduleClick(scheduleId: String) {

    }
}