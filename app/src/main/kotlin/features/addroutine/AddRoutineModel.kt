package features.addroutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import data.RoutineData
import enums.RepeatType
import mvi.MviModel
import usecase.ConvertRoutinesListToJson
import usecase.GetRoutinesMemory
import usecase.PutRoutineMemory
import usecase.WriteRoutinesToStorage
import util.emit
import javax.inject.Inject

class AddRoutineModel @Inject constructor(
        private val putRoutineMemory: PutRoutineMemory,
        private val getRoutinesMemory: GetRoutinesMemory,
        private val convertRoutinesToJson: ConvertRoutinesListToJson,
        private val writeRoutinesToStorage: WriteRoutinesToStorage)
    : MviModel<AddRoutineIntent, AddRoutineViewState> {
    private var idCount = 0L

    private val newStateData = MutableLiveData<AddRoutineViewState>()
    override val stateData: LiveData<AddRoutineViewState>
        get() = newStateData

    override fun handleIntent(intent: AddRoutineIntent, currentState: AddRoutineViewState?) {
        when (intent) {
            AddRoutineIntent.SaveClicked -> handleSaveRoutine(currentState!!)
            AddRoutineIntent.CancelledClicked -> newStateData.emit { currentState!!.copy(finished = true) }
            AddRoutineIntent.OnUserLeaving -> handleUserLeft()
            is AddRoutineIntent.TitleChanged -> handleTitleChanged(intent, currentState!!)
            is AddRoutineIntent.RepeatTypeChanged -> handleRepeatTypeChange(intent, currentState!!)
            is AddRoutineIntent.DayCheckedChange -> handleDayCheckChange(intent, currentState!!)
            is AddRoutineIntent.PresetData -> handlePresetData(intent)
        }
    }

    private fun handlePresetData(intent: AddRoutineIntent.PresetData) =
        newStateData.emit {
            (AddRoutineViewState(
                intent.routineData.description,
                intent.routineData.type,
                intent.routineData.type == RepeatType.Weekly,
                intent.routineData.days,
                true))
        }

    private fun handleDayCheckChange(intent: AddRoutineIntent.DayCheckedChange, state: AddRoutineViewState) {
        val newDays =
                if (intent.checked) {
                    state.daysSelected + intent.dayOfWeek
                } else {
                    state.daysSelected - intent.dayOfWeek
                }
        newStateData.emit { (setupSaveButtonState(state.copy(daysSelected = newDays))) }
    }

    private fun handleRepeatTypeChange(intent: AddRoutineIntent.RepeatTypeChanged, state: AddRoutineViewState) =
            when (intent.repeatType) {
                RepeatType.Daily ->
                    newStateData.emit { (setupSaveButtonState(state.copy(repeatType = RepeatType.Daily, daysVisible = false))) }
                RepeatType.Weekly ->
                    newStateData.emit { (setupSaveButtonState(state.copy(repeatType = RepeatType.Weekly, daysVisible = true))) }
            }

    private fun handleTitleChanged(intent: AddRoutineIntent.TitleChanged, state: AddRoutineViewState) =
        newStateData.emit { (setupSaveButtonState(state.copy(title = intent.title))) }


    private fun setupSaveButtonState(state: AddRoutineViewState): AddRoutineViewState =
            state.copy(saveEnabled =
            !(state.title.isEmpty() || (state.repeatType == RepeatType.Weekly && state.daysSelected.isEmpty())))


    private fun handleSaveRoutine(state: AddRoutineViewState) {
        putRoutineMemory(getNewRoutineData(state))
        newStateData.emit { (state.copy(finished = true)) }
    }

    private fun handleUserLeft() {
        writeRoutinesToStorage(convertRoutinesToJson(getRoutinesMemory()))
    }

    private fun getNewRoutineData(state: AddRoutineViewState): RoutineData =
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

    private fun generateNewId(state: AddRoutineViewState): Long {
        return state.routineData?.id ?: System.currentTimeMillis() + idCount.also { idCount++ }
    }
}