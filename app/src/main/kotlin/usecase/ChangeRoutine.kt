package usecase

import data.RoutineData
import data.RoutinesListData
import enums.DayOfWeek
import enums.RepeatType
import javax.inject.Inject

class ToggleCompleteRoutine @Inject constructor(
        private val dayOfWeekFromTime: DayOfWeekFromTime) {
    operator fun invoke(data: RoutineData, timeCompleted: Long): RoutineData =
            when (data.type) {
                RepeatType.Daily -> {
                    data.copy(
                            completed = !data.completed,
                            lastCompletedTimestamp = if (!data.completed) timeCompleted else 0)
                }
                RepeatType.Weekly -> {
                    val dayCompleted = dayOfWeekFromTime(timeCompleted)
                    val newDays =
                            if (data.completedDays.contains(dayCompleted)) {
                                data.completedDays - dayCompleted
                            } else {
                                data.completedDays + dayCompleted
                            }
                    val completedTimestamp =
                            if (newDays.size > data.completedDays.size) {
                                timeCompleted
                            } else {
                                0L
                            }
                    data.copy(completedDays = newDays, lastCompletedTimestamp = completedTimestamp)
                }
            }

}


class FilterRoutines @Inject constructor(private val dayOfWeekFromTime: DayOfWeekFromTime) {
    operator fun invoke(routineData: RoutinesListData, byCurrentTime: Long): RoutinesListData {
        val dayOfWeek = dayOfWeekFromTime(byCurrentTime)
        return RoutinesListData(
            routineData.routines
                .filter {
                    it.type == RepeatType.Daily ||
                        (it.type == RepeatType.Weekly && it.days.contains(dayOfWeek))
                }
                .sortedBy { it.description }
                .toSet()
        )
    }
}

class ResetRoutinesInMemory @Inject constructor(
    private val resetRoutines: ResetRoutines,
    private val getRoutinesMemory: GetRoutinesMemory,
    private val setRoutinesMemory: SetRoutinesMemory) {
    operator fun invoke(currentTime: Long) {
        setRoutinesMemory(resetRoutines(getRoutinesMemory(), currentTime))
    }
}

class ResetRoutines @Inject constructor(private val dayOfWeekFromTime: DayOfWeekFromTime) {
    operator fun invoke(routinesData: RoutinesListData, currentTime: Long): RoutinesListData {
        val currentDay = dayOfWeekFromTime(currentTime)
        return RoutinesListData(routinesData.routines.map { data ->
            when (data.type) {
                RepeatType.Daily -> resetDailyRoutine(data, currentDay)
                RepeatType.Weekly -> resetWeeklyRoutine(data, currentDay)
            }
        }.toSet())
    }

    private fun resetDailyRoutine(data: RoutineData, currentDay: DayOfWeek): RoutineData =
            if (!data.completed || dayOfWeekFromTime(data.lastCompletedTimestamp) == currentDay) {
                data
            } else {
                data.copy(completed = false, lastCompletedTimestamp = 0L)
            }

    private fun resetWeeklyRoutine(data: RoutineData, currentDay: DayOfWeek): RoutineData =
            if (data.completedDays.isEmpty()) {
                data
            } else {
                data.copy(completedDays = data.completedDays.filter { it == currentDay }.toSet())
            }
}
