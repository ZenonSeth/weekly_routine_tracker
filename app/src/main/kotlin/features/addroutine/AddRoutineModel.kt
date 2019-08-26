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
            is AddRoutineIntent.DayCheckedChange -> handleDayCheckChange(intent, state)
        }
    }

    private fun handleDayCheckChange(intent: AddRoutineIntent.DayCheckedChange, state: AddRoutineViewState) {
        val newDays =
                if (intent.checked) {
                    state.daysSelected + intent.dayOfWeek
                } else {
                    state.daysSelected - intent.dayOfWeek
                }
        render(setupSaveButtonState(state.copy(daysSelected = newDays)))
    }

    private fun handleRepeatTypeChange(intent: AddRoutineIntent.RepeatTypeChanged, state: AddRoutineViewState) =
            when (intent.repeatType) {
                RepeatType.Daily ->
                    render(setupSaveButtonState(state.copy(repeatType = RepeatType.Daily, daysVisible = false)))
                RepeatType.Weekly ->
                    render(setupSaveButtonState(state.copy(repeatType = RepeatType.Weekly, daysVisible = true)))
            }

    private fun handleTitleChanged(intent: AddRoutineIntent.TitleChanged, state: AddRoutineViewState) =
            render(setupSaveButtonState(state.copy(title = intent.title)))


    private fun setupSaveButtonState(state: AddRoutineViewState): AddRoutineViewState =
            state.copy(saveEnabled =
            !(state.title.isEmpty() || (state.repeatType == RepeatType.Weekly && state.daysSelected.isEmpty())))


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
                            type = RepeatType.Weekly,
                            days = state.daysSelected)
            }

    private fun generateNewId(): Long {
        return System.currentTimeMillis() + idCount.also { idCount++ }
    }
}