package features.showroutines

import data.RoutineData

sealed class ShowRoutinesIntent {
    object OnStartingUp : ShowRoutinesIntent()
    object OnResuming : ShowRoutinesIntent()
    object OnPausing : ShowRoutinesIntent()
    object OnShuttingDown : ShowRoutinesIntent()
    object AddNewRoutine : ShowRoutinesIntent()
    class OnItemLongClick(val data: RoutineData) : ShowRoutinesIntent()
    class OnItemClick(val data: RoutineData) : ShowRoutinesIntent()
}