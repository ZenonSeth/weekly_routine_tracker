package features.showroutines

import data.RoutinesListData

data class ShowRoutinesViewState(
        val routinesList: RoutinesListData,
        val addNewRoutine: Boolean = false
) {
    companion object {
        val Initial = ShowRoutinesViewState(RoutinesListData())
    }
}