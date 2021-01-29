package dev.hankli.iamstar.ui.schedule

import dev.hankli.iamstar.utils.ArchViewModel
import java.util.*

class ScheduleViewModel : ArchViewModel() {

    fun getSchedules(): List<Schedule> {
        return listOf(
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館"),
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館"),
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館"),
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館"),
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館"),
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館"),
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館"),
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館"),
            Schedule(Date(), "2021 愛美星百大女神璀璨之夜", "88號樂章 婚宴會館")
        )
    }
}