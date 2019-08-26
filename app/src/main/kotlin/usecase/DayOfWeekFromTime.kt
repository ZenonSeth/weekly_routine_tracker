package usecase

import enums.DayOfWeek
import java.util.*
import javax.inject.Inject

class DayOfWeekFromTime @Inject constructor() {
    operator fun invoke(timestamp: Long): DayOfWeek =
            Calendar.getInstance()
                    .apply { time = Date(timestamp) }
                    .get(Calendar.DAY_OF_WEEK)
                    .let {
                        when (it) {
                            Calendar.SUNDAY -> DayOfWeek.Sun
                            Calendar.MONDAY -> DayOfWeek.Mon
                            Calendar.TUESDAY -> DayOfWeek.Tue
                            Calendar.WEDNESDAY -> DayOfWeek.Wed
                            Calendar.THURSDAY -> DayOfWeek.Thu
                            Calendar.FRIDAY -> DayOfWeek.Fri
                            Calendar.SATURDAY -> DayOfWeek.Sat
                            else -> DayOfWeek.Mon
                        }
                    }
}