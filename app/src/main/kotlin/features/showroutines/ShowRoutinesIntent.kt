package features.showroutines

sealed class ShowRoutinesIntent {
    object OnStartingUp : ShowRoutinesIntent()
    object OnResuming : ShowRoutinesIntent()
    object OnPausing : ShowRoutinesIntent()
    object OnShuttingDown : ShowRoutinesIntent()
    object AddNewRoutine : ShowRoutinesIntent()
}