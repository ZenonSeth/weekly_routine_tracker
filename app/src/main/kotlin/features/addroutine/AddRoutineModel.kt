package features.addroutine

import data.DailyRoutineData
import data.RoutineData
import data.WeeklyRoutineData
import enums.RepeatType
import mvi.MviModel
import mvi.MviView
import usecase.ConvertRoutinesListToJson
import usecase.GetRoutines
import usecase.PutRoutine
import usecase.WriteRoutinesToStorage
import javax.inject.Inject

class AddRoutineModel @Inject constructor(
        private val putRoutine: PutRoutine,
        private val getRoutines: GetRoutines,
        private val convertRoutinesToJson: ConvertRoutinesListToJson,
        private val saveRoutines: WriteRoutinesToStorage)
    : MviModel<AddRoutineIntent, AddRoutineViewState>() {

    private var render: (AddRoutineViewState) -> Unit = {}
    private var idCount = 0L

    override fun attachViewModel(viewModel: MviView<AddRoutineIntent, AddRoutineViewState>) {
        viewModel.observeIntent(::handleIntent)
        render = { viewModel.render(it) }
    }

    private fun handleIntent(intent: AddRoutineIntent, state: AddRoutineViewState) {
        when (intent) {
            AddRoutineIntent.SaveClicked -> handleSaveRoutine(state)
            AddRoutineIntent.CancelledClicked -> render(state.copy(finished = true))
            AddRoutineIntent.OnUserLeaving -> handleUserLeft(state)
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
        putRoutine(getNewRoutineData(state))
        render(state.copy(finished = true))
    }

    private fun handleUserLeft(state: AddRoutineViewState) {
        saveRoutines(convertRoutinesToJson(getRoutines()))
    }

    private fun getNewRoutineData(state: AddRoutineViewState): RoutineData =
            when (state.repeatType) {
                RepeatType.Daily ->
                    DailyRoutineData(
                            state.title,
                            false,
                            0L,
                            generateNewId()
                    )
                RepeatType.Weekly ->
                    WeeklyRoutineData(
                            state.title,
                            state.daysSelected,
                            emptySet(),
                            0L,
                            generateNewId()
                    )
            }

    private fun generateNewId(): Long {
        return System.currentTimeMillis() + idCount.also { idCount++ }
    }
}