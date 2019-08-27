package features.showroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mvi.MviModel
import usecase.*
import util.emit
import javax.inject.Inject

class ShowRoutinesModel @Inject constructor(
        private val getRoutinesMemory: GetRoutinesMemory,
        private val setRoutinesMemory: SetRoutinesMemory,
        private val removeRoutineMemory: RemoveRoutineMemory,
        private val readRoutinesFromStorage: ReadRoutinesFromStorage,
        private val writeRoutinesToStorage: WriteRoutinesToStorage,
        private val routinesListToJson: ConvertRoutinesListToJson,
        private val jsonToRoutinesList: ConvertJsonToRoutinesList
) : MviModel<ShowRoutinesIntent, ShowRoutinesViewState>() {

    private val newStateData = MutableLiveData<ShowRoutinesViewState>()
    override val stateData: LiveData<ShowRoutinesViewState>
        get() = newStateData

    override fun handleIntent(intent: ShowRoutinesIntent, state: ShowRoutinesViewState?) {
        when (intent) {
            is ShowRoutinesIntent.OnStartingUp -> handleStartingUp()
            is ShowRoutinesIntent.OnResuming -> handleResuming()
            is ShowRoutinesIntent.OnPausing -> handlePausing()
            is ShowRoutinesIntent.OnShuttingDown -> handleShuttingDown()
            is ShowRoutinesIntent.AddNewRoutine -> handleNewRoutine(state!!)
            is ShowRoutinesIntent.OnItemLongClick -> handleItemLongClick(intent)
            is ShowRoutinesIntent.OnItemClick -> handleItemClick(intent, state!!)
        }
    }

    private fun handleItemLongClick(intent: ShowRoutinesIntent.OnItemLongClick) {
        removeRoutineMemory(intent.data.id)
        newStateData.emit { (ShowRoutinesViewState(getRoutinesMemory())) }
    }

    private fun handleItemClick(intent: ShowRoutinesIntent.OnItemClick, state: ShowRoutinesViewState) {
        newStateData.emit { (ShowRoutinesViewState(routinesList = state.routinesList, editRoutine = intent.data)) }
    }

    private fun handleStartingUp() {
        if (getRoutinesMemory().routines.isEmpty()) {
            setRoutinesMemory(jsonToRoutinesList(readRoutinesFromStorage()))
        }
    }

    private fun handleResuming() {
        newStateData.emit { (ShowRoutinesViewState(getRoutinesMemory())) }
    }

    private fun handlePausing() {
        // hmmm...
    }

    private fun handleShuttingDown() {
        writeRoutinesToStorage(routinesListToJson(getRoutinesMemory()))
    }

    private fun handleNewRoutine(state: ShowRoutinesViewState) {
        newStateData.emit { (ShowRoutinesViewState(state.routinesList, true)) }
    }

}