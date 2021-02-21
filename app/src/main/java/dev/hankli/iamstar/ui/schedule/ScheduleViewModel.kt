package dev.hankli.iamstar.ui.schedule

import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.data.models.Schedule
import dev.hankli.iamstar.repo.ProfileRepo
import dev.hankli.iamstar.repo.ScheduleRepo
import dev.hankli.iamstar.utils.ArchViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel(
    private val profileRepo: ProfileRepo,
    private val scheduleRepo: ScheduleRepo
) : ArchViewModel() {

    val refreshSchedulesCode = 1

    fun getScheduleOptions(influencerId: String) = FirestoreRecyclerOptions.Builder<Schedule>()
        .setQuery(scheduleRepo.queryByInfluencer(influencerId)) { snapshot ->
            val schedule = snapshot.toObject(Schedule::class.java)!!

            viewModelScope.launch(Main) {
                withContext(IO) {
                    schedule.photoURL = profileRepo.getPhotoURL(influencerId)
                }
                notifyView(refreshSchedulesCode)
            }

            return@setQuery schedule
        }
        .build()
}