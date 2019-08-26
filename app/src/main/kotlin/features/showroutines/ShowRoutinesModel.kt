package features.showroutines

import androidx.lifecycle.Observer
import mvi.MviModel
import mvi.MviView
import usecase.*
import javax.inject.Inject

class ShowRoutinesModel @Inject constructor(
        private val getRoutinesMemory: GetRoutinesMemory,
        private val setRoutinesMemory: SetRoutinesMemory,
        private val readRoutinesFromStorage: ReadRoutinesFromStorage,
        private val writeRoutinesToStorage: WriteRoutinesToStorage,
        private val routinesListToJson: ConvertRoutinesListToJson,
        private val jsonToRoutinesList: ConvertJsonToRoutinesList
) : MviModel<ShowRoutinesIntent, ShowRoutinesViewState>() {

    private var render: (ShowRoutinesViewState) -> Unit = {}
    private val observer = Observer<Pair<ShowRoutinesIntent, ShowRoutinesViewState>> { handleIntent(it.first, it.second) }

    override fun attachViewModel(viewModel: MviView<ShowRoutinesIntent, ShowRoutinesViewState>) {
        viewModel.observeIntent(observer)
        render = viewModel::render
    }

    private fun handleIntent(intent: ShowRoutinesIntent, state: ShowRoutinesViewState) {
        when (intent) {
            is ShowRoutinesIntent.OnStartingUp -> handleStartingUp()
            is ShowRoutinesIntent.OnResuming -> handleResuming()
            is ShowRoutinesIntent.OnPausing -> handlePausing()
            is ShowRoutinesIntent.OnShuttingDown -> handleShuttingDown()
            is ShowRoutinesIntent.AddNewRoutine -> handleNewRoutine(state)
        }
    }

    private fun handleStartingUp() {
        if (getRoutinesMemory().routines.isEmpty()) {
            setRoutinesMemory(jsonToRoutinesList(readRoutinesFromStorage()))
        }
    }

    private fun handleResuming() {
        render(ShowRoutinesViewState(getRoutinesMemory()))
    }

    private fun handlePausing() {
        // hmmm...
    }

    private fun handleShuttingDown() {
        writeRoutinesToStorage(routinesListToJson(getRoutinesMemory()))
    }

    private fun handleNewRoutine(state: ShowRoutinesViewState) {
        render(ShowRoutinesViewState(state.routinesList, true))
    }

}