package features.addroutine

import androidx.lifecycle.Observer
import data.RoutineData
import enums.RepeatType
import mvi.MviModel
import mvi.MviView
import usecase.ConvertRoutinesListToJson
import usecase.GetRoutinesMemory
import usecase.PutRoutineMemory
import usecase.WriteRoutinesToStorage
import javax.inject.Inject

class AddRoutineModel @Inject constructor(
        private val putRoutineMemory: PutRoutineMemory,
        private val getRoutinesMemory: GetRoutinesMemory,
        private val convertRoutinesToJson: ConvertRoutinesListToJson,
        private val writeRoutinesToStorage: WriteRoutinesToStorage)
    : MviModel<AddRoutineIntent, AddRoutineViewState>() {

    private var render: (AddRoutineViewState) -> Unit = {}
    private var idCount = 0L
    private val observer = Observer<Pair<AddRoutineIntent, AddRoutineViewState>> { handleIntent(it.first, it.second) }

    override fun attachViewModel(viewModel: MviView<AddRoutineIntent, AddRoutineViewState>) {
        viewModel.observeIntent(observer)
        render = viewModel::render
    }

    private fun handleIntent(intent: AddRoutineIntent, state: AddRoutineViewState) {
        when (intent) {
            AddRoutineIntent.SaveClicked -> handleSaveRoutine(state)
            AddRoutineIntent.CancelledClicked -> render(state.copy(finished = true))
            AddRoutineIntent.OnUserLeaving -> handleUserLeft()
            is AddRoutineIntent.TitleChanged -> handleTitleChanged(intent, state)
            is AddRoutineIntent.RepeatTypeChanged -> handleRepeatTypeChange(intent, state)
        }
    }

    private fun handleRepeatTypeChange(intent: AddRoutineIntent.RepeatTypeChanged, state: AddRoutineViewState) =
            when (intent.repeatType) {
                RepeatType.Daily ->
                    render(state.copy(repeatType = RepeatType.Daily, daysVisible = false))
                RepeatType.Weekly ->
                    render(state.copy(repeatType = RepeatType.Weekly, daysVisible = true))
            }

    private fun handleTitleChanged(intent: AddRoutineIntent.TitleChanged, state: AddRoutineViewState) =
            render(state.copy(title = intent.title, saveEnabled = intent.title.isNotEmpty()))

    private fun handleSaveRoutine(state: AddRoutineViewState) {
        putRoutineMemory(getNewRoutineData(state))
        render(state.copy(finished = true))
    }

    private fun handleUserLeft() {
        writeRoutinesToStorage(convertRoutinesToJson(getRoutinesMemory()))
    }

    private fun getNewRoutineData(state: AddRoutineViewState): RoutineData =
            when (state.repeatType) {
                RepeatType.Daily ->
                    RoutineData(
                            id = generateNewId(),
                            description = state.title,
                            type = RepeatType.Daily)
                RepeatType.Weekly ->
                    RoutineData(
                            id = generateNewId(),
                            description = state.title,
                            type = RepeatType.Weekly)
            }

    private fun generateNewId(): Long {
        return System.currentTimeMillis() + idCount.also { idCount++ }
    }
}