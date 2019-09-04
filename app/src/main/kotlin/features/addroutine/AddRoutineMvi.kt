package features.addroutine

import data.RoutineData
import enums.DayOfWeek
import enums.RepeatType

sealed class AddRoutineIntent {
    object SaveClicked : AddRoutineIntent()
    object CancelledClicked : AddRoutineIntent()
    object OnUserLeaving : AddRoutineIntent()
    class TitleChanged(val title: String) : AddRoutineIntent()
    class RepeatTypeChanged(val repeatType: RepeatType) : AddRoutineIntent()
    class DayCheckedChange(val dayOfWeek: DayOfWeek, val checked: Boolean) : AddRoutineIntent()
    class PresetData(val routineData: RoutineData) : AddRoutineIntent()
}

sealed class AddRoutineEvent {
    object Finish : AddRoutineEvent()
}

data class AddRoutineState(
    val title: String,
    val repeatType: RepeatType,
    val daysVisible: Boolean,
    val daysSelected: Set<DayOfWeek>,
    val saveEnabled: Boolean,
    val routineData: RoutineData? = null
) {
    companion object {
        val Initial = AddRoutineState("", RepeatType.Daily, false, emptySet(), false)
    }
}