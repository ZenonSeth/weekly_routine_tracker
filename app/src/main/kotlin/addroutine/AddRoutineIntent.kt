package addroutine

import enums.RepeatType

sealed class AddRoutineIntent {
    object SaveClicked : AddRoutineIntent()
    object CancelledClicked : AddRoutineIntent()
    class TitleChanged(val title: String) : AddRoutineIntent()
    class RepeatTypeChanged(val repeatType: RepeatType) : AddRoutineIntent()

}