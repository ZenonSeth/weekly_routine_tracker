package addroutine

import enums.RepeatType
import mvi.MviModel
import mvi.MviView

class AddRoutineModel : MviModel<AddRoutineIntent, AddRoutineViewState>() {

    private var render: (AddRoutineViewState) -> Unit = {}

    override fun attachViewModel(viewModel: MviView<AddRoutineIntent, AddRoutineViewState>) {
        viewModel.observeIntent(::handleIntent)
        render = { viewModel.render(it) }
    }

    private fun handleIntent(intent: AddRoutineIntent, state: AddRoutineViewState) {
        when (intent) {
            AddRoutineIntent.SaveClicked -> handleSaveRoutine(state)
            AddRoutineIntent.CancelledClicked -> render(state.copy(finished = true))
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}