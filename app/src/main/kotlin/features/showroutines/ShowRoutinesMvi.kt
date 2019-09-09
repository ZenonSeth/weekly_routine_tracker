package features.showroutines

import data.RoutineData
import data.RoutinesListData

sealed class ShowRoutinesIntent {
    object OnStartingUp : ShowRoutinesIntent()
    object OnShuttingDown : ShowRoutinesIntent()
    object AddNewRoutine : ShowRoutinesIntent()
    class OnItemLongClick(val data: RoutineData) : ShowRoutinesIntent()
    class OnItemClick(val data: RoutineData) : ShowRoutinesIntent()
}

sealed class ShowRoutinesEvent {
    object AddNewRoutine: ShowRoutinesEvent()
    data class EditRoutine(val data: RoutineData): ShowRoutinesEvent()
}

data class ShowRoutinesState(val routinesList: RoutinesListData) {
    companion object {
        val Initial = ShowRoutinesState(RoutinesListData())
    }
}