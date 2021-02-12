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

    private val scheduleAdapter = ScheduleAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Role Permission
        val writable = AuthManager.currentUserId == app.influencerId
        setMenuVisibility(writable)

        view_schedule_list.apply {
            setHasFixedSize(true)
            adapter = scheduleAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        }

        scheduleAdapter.item = viewModel.getSchedules()
        scheduleAdapter.notifyDataSetChanged()
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
}