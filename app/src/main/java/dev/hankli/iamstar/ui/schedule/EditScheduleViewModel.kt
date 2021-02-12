package dev.hankli.iamstar.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.datepicker.MaterialDatePicker
import dev.hankli.iamstar.utils.ArchViewModel
import dev.hankli.iamstar.utils.getClearedCal
import java.util.*

class EditScheduleViewModel : ArchViewModel() {

    private val _startDateTime = MutableLiveData<Calendar>()
    val startDateTime: LiveData<Calendar>
        get() = _startDateTime

    private val _endDateTime = MutableLiveData<Calendar>()
    val endDateTime: LiveData<Calendar>
        get() = _endDateTime

    fun loadSchedule(scheduleId: String) {

        initStartDateTime()
        initEndDateTime()
    }

    private fun initStartDateTime(startDateTime: Date? = null) {
        val cal = getClearedCal()
        _startDateTime.value = if (startDateTime != null) {
            cal.time = startDateTime
            cal
        } else {
            cal.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
            cal.set(Calendar.HOUR_OF_DAY, 8)
            cal
        }
    }

    private fun initEndDateTime(endDateTime: Date? = null) {
        val cal = getClearedCal()
        _endDateTime.value = if (endDateTime != null) {
            cal.time = endDateTime
            cal
        } else {
            cal.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal
        }
    }

    fun getStartDateInMillis() = _startDateTime.value!!.timeInMillis

    fun getStartHour() = _startDateTime.value!!.get(Calendar.HOUR_OF_DAY)

    fun getStartMinute() = _startDateTime.value!!.get(Calendar.MINUTE)

    fun setStartDateInMillis(date: Long) {
        val sDateTime = _startDateTime.value!!
        sDateTime.timeInMillis = date
        _startDateTime.value = sDateTime
    }

    fun setStartTime(hour: Int, minute: Int) {
        val sDateTime = _startDateTime.value!!
        sDateTime.set(Calendar.HOUR_OF_DAY, hour)
        sDateTime.set(Calendar.MINUTE, minute)
        _startDateTime.value = sDateTime
    }

    fun getEndDateInMillis() = _endDateTime.value!!.timeInMillis

    fun getEndHour() = _endDateTime.value!!.get(Calendar.HOUR_OF_DAY)

    fun getEndMinute() = _endDateTime.value!!.get(Calendar.MINUTE)

    fun setEndDateInMillis(date: Long) {
        val eDateTime = _endDateTime.value!!
        eDateTime.timeInMillis = date
        _endDateTime.value = eDateTime
    }

    fun setEndTime(hour: Int, minute: Int) {
        val eDateTime = _endDateTime.value!!
        eDateTime.set(Calendar.HOUR_OF_DAY, hour)
        eDateTime.set(Calendar.MINUTE, minute)
        _endDateTime.value = eDateTime
    }
}