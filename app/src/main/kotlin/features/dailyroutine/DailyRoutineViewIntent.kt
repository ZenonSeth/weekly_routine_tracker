package features.dailyroutine

import data.RoutineData

sealed class DailyRoutineViewIntent {
    object ManageButtonClick : DailyRoutineViewIntent()
    object OnShuttingDown : DailyRoutineViewIntent()
    object OnStartingUp : DailyRoutineViewIntent()
    object OnResuming : DailyRoutineViewIntent()
    class ItemClicked(val data: RoutineData) : DailyRoutineViewIntent()
}