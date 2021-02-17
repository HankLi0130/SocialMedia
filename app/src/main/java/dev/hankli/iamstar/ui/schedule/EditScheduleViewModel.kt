package dev.hankli.iamstar.ui.schedule

import android.content.ContentResolver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Schedule
import dev.hankli.iamstar.repo.ProfileRepo
import dev.hankli.iamstar.utils.ArchViewModel
import dev.hankli.iamstar.utils.media.MediaFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

class EditScheduleViewModel(private val profileRepo: ProfileRepo) : ArchViewModel() {

    private lateinit var schedule: Schedule

    private val _titleData = MutableLiveData<String?>()
    val titleData get() = _titleData

    private val _startDateTime = MutableLiveData<Date>()
    val startDateTime get() = _startDateTime

    private val _endDateTime = MutableLiveData<Date>()
    val endDateTime get() = _endDateTime

    private val _locationData = MutableLiveData<String?>()
    val locationData get() = _locationData

    private var image: MediaFile? = null

    private val _imageData = MutableLiveData<MediaFile?>()
    val imageData get() = _imageData

    fun loadSchedule(scheduleId: String) {
        if (scheduleId == EMPTY) {
            schedule = Schedule()
            setDefaultValues()
        } else {
            viewModelScope.launch {
                callProgress(true)
                withContext(Dispatchers.IO) { }
                setDefaultValues()
                callProgress(false)
            }
        }
    }

    private fun setDefaultValues() {
        _titleData.value = schedule.title
        _startDateTime.value = schedule.startDateTime
        _endDateTime.value = schedule.endDateTime
        _locationData.value = schedule.location
    }

    suspend fun getPhotoURL(userId: String): String? = profileRepo.getPhotoURL(userId)

    fun getStartDateInMillis() = schedule.startDateTime.time

    fun getStartHour() = schedule.startDateTime.hours

    fun getStartMinute() = schedule.startDateTime.minutes

    fun setStartDateInMillis(date: Long) {
        _startDateTime.value = schedule.startDateTime.apply {
            time = date
        }
    }

    fun setStartTime(hour: Int, minute: Int) {
        _startDateTime.value = schedule.startDateTime.apply {
            hours = hour
            minutes = minute
        }
    }

    fun getEndDateInMillis() = schedule.endDateTime.time

    fun getEndHour() = schedule.endDateTime.hours

    fun getEndMinute() = schedule.endDateTime.minutes

    fun setEndDateInMillis(date: Long) {
        _endDateTime.value = schedule.endDateTime.apply {
            time = date
        }
    }

    fun setEndTime(hour: Int, minute: Int) {
        _endDateTime.value = schedule.endDateTime.apply {
            hours = hour
            minutes = minute
        }
    }

    fun onTitleChange(text: CharSequence?) {
        val title = text?.toString() ?: EMPTY
        schedule.title = title
    }

    fun setLocation(location: String?, latitude: Double?, longitude: Double?) {
        schedule.location = location
        schedule.latitude = latitude
        schedule.longitude = longitude
        _locationData.value = schedule.location
    }

    fun isImageSelected() = image != null

    fun addImage(image: MediaFile) {
        this.image = image
        _imageData.value = this.image
    }

    fun removeImage() {
        this.image = null
        _imageData.value = this.image
    }

    fun submit(
        contentResolver: ContentResolver,
        influencerId: String
    ) {
        val errorMessageRes = checkValid()
        if (errorMessageRes.isNotEmpty()) {
            showErrors(errorMessageRes)
            return
        }
    }

    private fun checkValid(): List<Int> {
        val errorMessageRes = mutableListOf<Int>()
        if (schedule.title.isNullOrEmpty()) errorMessageRes.add(R.string.error_schedule_title_is_empty)
        return errorMessageRes

    }
}