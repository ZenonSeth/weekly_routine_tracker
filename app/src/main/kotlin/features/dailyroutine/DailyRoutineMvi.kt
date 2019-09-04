package features.dailyroutine

import data.RoutineData
import data.RoutinesListData

sealed class DailyRoutineIntent {
    object ManageButtonClick : DailyRoutineIntent()
    object OnShuttingDown : DailyRoutineIntent()
    object OnStartingUp : DailyRoutineIntent()
    class ItemClicked(val data: RoutineData) : DailyRoutineIntent()
}

sealed class DailyRoutineEvent {
    object GoToManageRoutineScreen : DailyRoutineEvent()
}

data class DailyRoutineState(
    val routinesList: RoutinesListData) {
    companion object {
        val Initial = DailyRoutineState(RoutinesListData())
    }
}
