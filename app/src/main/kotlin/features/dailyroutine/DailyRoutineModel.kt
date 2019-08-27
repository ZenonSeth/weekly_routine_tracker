package features.dailyroutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import data.RoutinesListData
import mvi.MviModel
import usecase.*
import util.emit
import javax.inject.Inject

class DailyRoutineModel @Inject constructor(
        private val readRoutinesFromStorage: ReadRoutinesFromStorage,
        private val writeRoutinesToStorage: WriteRoutinesToStorage,
        private val setRoutinesMemory: SetRoutinesMemory,
        private val getRoutinesMemory: GetRoutinesMemory,
        private val putRoutineMemory: PutRoutineMemory,
        private val routinesListToJson: ConvertRoutinesListToJson,
        private val jsonToRoutinesList: ConvertJsonToRoutinesList,
        private val filterRoutines: FilterRoutines,
        private val dayOfWeekFromTime: DayOfWeekFromTime,
        private val completeRoutineToggle: ToggleCompleteRoutine,
        private val resetRoutines: ResetRoutines)
    : MviModel<DailyRoutineViewIntent, DailyRoutineViewState>() {

    private val newStateData = MutableLiveData<DailyRoutineViewState>()
    override val stateData: LiveData<DailyRoutineViewState>
        get() = newStateData

    override fun handleIntent(intent: DailyRoutineViewIntent, currentState: DailyRoutineViewState?) {
        when (intent) {
            DailyRoutineViewIntent.OnStartingUp -> handleStartingUp()
            DailyRoutineViewIntent.OnShuttingDown -> handleShuttingDown()
            DailyRoutineViewIntent.OnResuming -> handleOnResuming()
            DailyRoutineViewIntent.ManageButtonClick -> handleManageButtonClick(currentState!!)
            is DailyRoutineViewIntent.ItemClicked -> handleItemClicked(intent)
        }
    }

    private fun handleStartingUp() {
        if (getRoutinesMemory().routines.isEmpty()) {
            setRoutinesMemory(jsonToRoutinesList(readRoutinesFromStorage()))
        }
    }

    private fun handleShuttingDown() {
        setRoutinesMemory(resetRoutines(getRoutinesMemory(), System.currentTimeMillis()))
        writeRoutinesToStorage(routinesListToJson(getRoutinesMemory()))
    }

    private fun handleOnResuming() {
        newStateData.emit { (DailyRoutineViewState(applyFilter(getRoutinesMemory()))) }
    }

    private fun handleManageButtonClick(state: DailyRoutineViewState) {
        newStateData.emit { (DailyRoutineViewState(state.routinesList, true)) }
    }

    private fun handleItemClicked(intent: DailyRoutineViewIntent.ItemClicked) {
        putRoutineMemory(completeRoutineToggle(intent.data, System.currentTimeMillis()))
        newStateData.emit { (DailyRoutineViewState(applyFilter(getRoutinesMemory()))) }
    }

    private fun applyFilter(data: RoutinesListData) =
            filterRoutines(data, dayOfWeekFromTime(System.currentTimeMillis()))
}