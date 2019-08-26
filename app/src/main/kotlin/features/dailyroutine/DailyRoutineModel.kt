package features.dailyroutine

import androidx.lifecycle.Observer
import data.RoutinesListData
import mvi.MviModel
import mvi.MviView
import usecase.*
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

    private var render: (DailyRoutineViewState) -> Unit = {}
    private val observer =
            Observer<Pair<DailyRoutineViewIntent, DailyRoutineViewState>> { handleIntent(it.first, it.second) }

    override fun attachViewModel(viewModel: MviView<DailyRoutineViewIntent, DailyRoutineViewState>) {
        viewModel.observeIntent(observer)
        render = viewModel::render
    }

    private fun handleIntent(intent: DailyRoutineViewIntent, state: DailyRoutineViewState) {
        when (intent) {
            DailyRoutineViewIntent.OnStartingUp -> handleStartingUp()
            DailyRoutineViewIntent.OnShuttingDown -> handleShuttingDown()
            DailyRoutineViewIntent.OnResuming -> handleOnResuming()
            DailyRoutineViewIntent.ManageButtonClick -> handleManageButtonClick(state)
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
        render(DailyRoutineViewState(applyFilter(getRoutinesMemory())))
    }

    private fun handleManageButtonClick(state: DailyRoutineViewState) {
        render(DailyRoutineViewState(state.routinesList, true))
    }

    private fun handleItemClicked(intent: DailyRoutineViewIntent.ItemClicked) {
        putRoutineMemory(completeRoutineToggle(intent.data, System.currentTimeMillis()))
        render(DailyRoutineViewState(applyFilter(getRoutinesMemory())))
    }

    private fun applyFilter(data: RoutinesListData) =
            filterRoutines(data, dayOfWeekFromTime(System.currentTimeMillis()))
}