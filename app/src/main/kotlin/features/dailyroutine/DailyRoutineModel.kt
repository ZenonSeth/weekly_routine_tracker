package features.dailyroutine

import data.RoutinesListData
import kotlinx.coroutines.runBlocking
import mvi.MviModel
import usecase.*
import javax.inject.Inject

class DailyRoutineModel: MviModel<DailyRoutineIntent, DailyRoutineState, DailyRoutineEvent>() {
    @Inject lateinit var getRoutinesMemory: GetRoutinesMemory
    @Inject lateinit var putRoutineMemory: PutRoutineMemory
    @Inject lateinit var filterRoutines: FilterRoutines
    @Inject lateinit var completeRoutineToggle: ToggleCompleteRoutine
    @Inject lateinit var resetRoutinesInMemory: ResetRoutinesInMemory
    @Inject lateinit var loadRoutineStorageIntoMemory: LoadRoutineStorageIntoMemory
    @Inject lateinit var saveRoutineMemoryToStorage: SaveRoutineMemoryToStorage

    override fun getInitialState() = DailyRoutineState.Initial

    override fun handleIntent(intent: DailyRoutineIntent) {
        when (intent) {
            DailyRoutineIntent.OnStartingUp -> handleStartingUp()
            DailyRoutineIntent.OnShuttingDown -> handleShuttingDown()
            DailyRoutineIntent.ManageButtonClick -> handleManageButtonClick()
            is DailyRoutineIntent.ItemClicked -> handleItemClicked(intent)
        }
    }

    private fun handleStartingUp() = runIo {
        if (getRoutinesMemory().routines.isEmpty()) {
            loadRoutineStorageIntoMemory()
        }
        resetRoutinesInMemory(System.currentTimeMillis())
        emitState { it.copy(routinesList = applyFilter(getRoutinesMemory())) }
    }

    private fun handleShuttingDown() = runBlocking {
        resetRoutinesInMemory(System.currentTimeMillis())
        saveRoutineMemoryToStorage()
    }

    private fun handleManageButtonClick() {
        emitEvent { DailyRoutineEvent.GoToManageRoutineScreen }
    }

    private fun handleItemClicked(intent: DailyRoutineIntent.ItemClicked) {
        putRoutineMemory(completeRoutineToggle(intent.data, System.currentTimeMillis()))
        emitState { (DailyRoutineState(applyFilter(getRoutinesMemory()))) }
    }

    private fun applyFilter(data: RoutinesListData) =
        filterRoutines(data, System.currentTimeMillis())
}