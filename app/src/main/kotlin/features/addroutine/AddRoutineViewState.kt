package features.addroutine

import data.RoutineData
import enums.DayOfWeek
import enums.RepeatType

data class AddRoutineViewState(
        val title: String,
        val repeatType: RepeatType,
        val daysVisible: Boolean,
        val daysSelected: Set<DayOfWeek>,
        val saveEnabled: Boolean,
        val finished: Boolean = false,
        val routineData: RoutineData? = null
) {
    companion object {
        val Initial = AddRoutineViewState("", RepeatType.Daily, false, emptySet(), false)
    }
}