package features.dailyroutine

import data.RoutinesListData

data class DailyRoutineViewState(
        val routinesList: RoutinesListData,
        val manageRoutines: Boolean = false
) {
    companion object {
        val Initial = DailyRoutineViewState(RoutinesListData())
    }
}