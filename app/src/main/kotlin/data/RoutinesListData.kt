package data

import enums.DayOfWeek

sealed class RoutineData

data class DailyRoutineData(
        val description: String,
        val completed: Boolean,
        val lastCompletedTimestamp: Long
) : RoutineData()

data class WeeklyRoutineData(
        val description: String,
        val days: Set<DayOfWeek>,
        val completedDays: Set<DayOfWeek>,
        val lastCompletedTimestamp: Long
) : RoutineData()

data class RoutinesListData(
        val routines: List<RoutineData>
)