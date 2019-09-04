package features.showroutines

import kotlinx.coroutines.runBlocking
import mvi.MviModel
import usecase.GetRoutinesMemory
import usecase.LoadRoutineStorageIntoMemory
import usecase.RemoveRoutineMemory
import usecase.SaveRoutineMemoryToStorage
import javax.inject.Inject

class ShowRoutinesModel : MviModel<ShowRoutinesIntent, ShowRoutinesState, ShowRoutinesEvent>() {

    @Inject lateinit var removeRoutineMemory: RemoveRoutineMemory
    @Inject lateinit var loadRoutineStorageIntoMemory: LoadRoutineStorageIntoMemory
    @Inject lateinit var saveRoutineMemoryToStorage: SaveRoutineMemoryToStorage
    @Inject lateinit var getRoutinesMemory: GetRoutinesMemory

    override fun getInitialState() = ShowRoutinesState.Initial

    override fun handleIntent(intent: ShowRoutinesIntent) {
        when (intent) {
            is ShowRoutinesIntent.OnStartingUp -> handleStartingUp()
            is ShowRoutinesIntent.OnShuttingDown -> handleShuttingDown()
            is ShowRoutinesIntent.AddNewRoutine -> handleNewRoutine()
            is ShowRoutinesIntent.OnItemLongClick -> handleItemLongClick(intent)
            is ShowRoutinesIntent.OnItemClick -> handleItemClick(intent)
        }
    }

    private fun handleItemLongClick(intent: ShowRoutinesIntent.OnItemLongClick) = runIo {
        removeRoutineMemory(intent.data.id)
        emitState { (ShowRoutinesState(getRoutinesMemory())) }
    }

    private fun handleItemClick(intent: ShowRoutinesIntent.OnItemClick) {
        emitEvent { ShowRoutinesEvent.EditRoutine(intent.data) }
    }

    private fun handleStartingUp() = runIo {
        if (getRoutinesMemory().routines.isEmpty()) {
            loadRoutineStorageIntoMemory()
        }
        emitState { it.copy(routinesList = getRoutinesMemory()) }
    }

    private fun handleShuttingDown() = runBlocking {
        saveRoutineMemoryToStorage()
    }

    private fun handleNewRoutine() {
        emitEvent { ShowRoutinesEvent.AddNewRoutine }
    }

}