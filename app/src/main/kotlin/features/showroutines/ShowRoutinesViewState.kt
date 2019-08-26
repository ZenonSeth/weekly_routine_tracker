package features.showroutines

import data.RoutinesListData

data class ShowRoutinesViewState(
        val routinesList: RoutinesListData,
        val addNewItent: Boolean = false
) {
    companion object {
        val Initial = ShowRoutinesViewState(RoutinesListData(emptySet()))
    }
}