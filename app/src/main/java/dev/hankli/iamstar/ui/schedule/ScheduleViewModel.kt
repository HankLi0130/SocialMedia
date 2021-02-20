package dev.hankli.iamstar.ui.schedule

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.data.models.Schedule
import dev.hankli.iamstar.repo.ScheduleRepo
import dev.hankli.iamstar.utils.ArchViewModel

class ScheduleViewModel(private val scheduleRepo: ScheduleRepo) : ArchViewModel() {

    fun getScheduleOptions(influencerId: String) = FirestoreRecyclerOptions.Builder<Schedule>()
        .setQuery(scheduleRepo.queryByInfluencer(influencerId), Schedule::class.java)
        .build()
}