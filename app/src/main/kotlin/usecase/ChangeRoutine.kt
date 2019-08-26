package usecase

import data.DailyRoutineData
import data.WeeklyRoutineData
import storage.RoutineMemoryStore
import javax.inject.Inject

class CompleteRoutine @Inject constructor(
        private val routineMemoryStore: RoutineMemoryStore,
        private val dayOfWeekFromTime: DayOfWeekFromTime) {
    operator fun invoke(routineId: Long, timeCompleted: Long) {
        routineMemoryStore
                .getRoutinesData()
                .routines
                .firstOrNull { it.id == routineId }
                ?.let {
                    routineMemoryStore.putRoutine(
                            when (it) {
                                is DailyRoutineData ->
                                    it.copy(completed = true, lastCompletedTimestamp = timeCompleted)
                                is WeeklyRoutineData ->
                                    it.copy(
                                            completedDays = it.completedDays.plus(dayOfWeekFromTime(timeCompleted)),
                                            lastCompletedTimestamp = timeCompleted)
                            })
                }
    }

}