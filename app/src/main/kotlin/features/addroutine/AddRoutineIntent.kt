package features.addroutine

import enums.RepeatType

sealed class AddRoutineIntent {
    object SaveClicked : AddRoutineIntent()
    object CancelledClicked : AddRoutineIntent()
    object OnUserLeaving : AddRoutineIntent()
    class TitleChanged(val title: String) : AddRoutineIntent()
    class RepeatTypeChanged(val repeatType: RepeatType) : AddRoutineIntent()

}