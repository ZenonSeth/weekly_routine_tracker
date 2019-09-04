package features.addroutine

import data.RoutineData
import enums.RepeatType
import mvi.MviModel
import usecase.PutRoutineMemory
import usecase.SaveRoutineMemoryToStorage
import javax.inject.Inject

class AddRoutineModel : MviModel<AddRoutineIntent, AddRoutineState, AddRoutineEvent>() {
    @Inject lateinit var putRoutineMemory: PutRoutineMemory
    @Inject lateinit var saverRoutineMemoryToStorage: SaveRoutineMemoryToStorage
    private var idCount = 0L

    override fun getInitialState() = AddRoutineState.Initial

    override fun handleIntent(intent: AddRoutineIntent) {
        when (intent) {
            AddRoutineIntent.SaveClicked -> handleSaveRoutine()
            AddRoutineIntent.CancelledClicked -> emitEvent { AddRoutineEvent.Finish }
            AddRoutineIntent.OnUserLeaving -> handleUserLeft()
            is AddRoutineIntent.TitleChanged -> handleTitleChanged(intent)
            is AddRoutineIntent.RepeatTypeChanged -> handleRepeatTypeChange(intent)
            is AddRoutineIntent.DayCheckedChange -> handleDayCheckChange(intent)
            is AddRoutineIntent.PresetData -> handlePresetData(intent)
        }
    }

    private fun handlePresetData(intent: AddRoutineIntent.PresetData) =
        emitState {
            (AddRoutineState(
                intent.routineData.description,
                intent.routineData.type,
                intent.routineData.type == RepeatType.Weekly,
                intent.routineData.days,
                true,
                routineData = intent.routineData))
        }

    private fun handleDayCheckChange(intent: AddRoutineIntent.DayCheckedChange) {
        val newDays =
            if (intent.checked) {
                currentState.daysSelected + intent.dayOfWeek
            } else {
                currentState.daysSelected - intent.dayOfWeek
            }
        emitState { setupSaveButtonState(it.copy(daysSelected = newDays)) }
    }

    private fun handleRepeatTypeChange(intent: AddRoutineIntent.RepeatTypeChanged) =
        when (intent.repeatType) {
            RepeatType.Daily ->
                emitState {
                    (setupSaveButtonState(it.copy(repeatType = RepeatType.Daily,
                                                  daysVisible = false)))
                }
            RepeatType.Weekly ->
                emitState {
                    (setupSaveButtonState(it.copy(repeatType = RepeatType.Weekly,
                                                  daysVisible = true)))
                }
        }

    private fun handleTitleChanged(intent: AddRoutineIntent.TitleChanged) =
        emitState { (setupSaveButtonState(it.copy(title = intent.title))) }

    private fun setupSaveButtonState(state: AddRoutineState): AddRoutineState =
        state.copy(saveEnabled =
                   !(state.title.isEmpty() || (state.repeatType == RepeatType.Weekly && state.daysSelected.isEmpty())))

    private fun handleSaveRoutine() {
        putRoutineMemory(getNewRoutineData(currentState))
        emitEvent { AddRoutineEvent.Finish }
    }

    private fun handleUserLeft() {
        saverRoutineMemoryToStorage()
    }

    private fun getNewRoutineData(state: AddRoutineState): RoutineData =
        when (state.repeatType) {
            RepeatType.Daily ->
                RoutineData(
                    id = generateNewId(state),
                    description = state.title,
                    type = RepeatType.Daily)
            RepeatType.Weekly ->
                RoutineData(
                    id = generateNewId(state),
                    description = state.title,
                    type = RepeatType.Weekly,
                    days = state.daysSelected)
        }

    private fun generateNewId(state: AddRoutineState): Long {
        return state.routineData?.id ?: System.currentTimeMillis() + idCount.also { idCount++ }
    }
}