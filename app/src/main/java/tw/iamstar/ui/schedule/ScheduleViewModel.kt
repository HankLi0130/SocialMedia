package tw.iamstar.ui.schedule

import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.iamstar.data.models.firestore.Schedule
import tw.iamstar.repo.ProfileRepo
import tw.iamstar.repo.ScheduleRepo
import tw.iamstar.utils.ArchViewModel

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

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch(Main) {
            callProgress(true)
            withContext(IO) {
                scheduleRepo.removeSchedule(scheduleId)
            }
            callProgress(false)
        }
    }
}