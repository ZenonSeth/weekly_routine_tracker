package features.showroutines

import data.RoutineData
import data.RoutinesListData

data class ShowRoutinesViewState(
        val routinesList: RoutinesListData,
        val addNewRoutine: Boolean = false,
        val editRoutine: RoutineData? = null
) {
    companion object {
        val Initial = ShowRoutinesViewState(RoutinesListData())
    }
}