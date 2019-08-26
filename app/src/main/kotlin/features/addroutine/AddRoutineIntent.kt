package features.addroutine

import enums.DayOfWeek
import enums.RepeatType

sealed class AddRoutineIntent {
    object SaveClicked : AddRoutineIntent()
    object CancelledClicked : AddRoutineIntent()
    object OnUserLeaving : AddRoutineIntent()
    class TitleChanged(val title: String) : AddRoutineIntent()
    class RepeatTypeChanged(val repeatType: RepeatType) : AddRoutineIntent()
    class DayCheckedChange(val dayOfWeek: DayOfWeek, val checked: Boolean) : AddRoutineIntent()
}