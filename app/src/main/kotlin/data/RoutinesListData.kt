package data

import enums.DayOfWeek

sealed class RoutineData(val id: Long)

data class DailyRoutineData(
        val description: String,
        val completed: Boolean,
        val lastCompletedTimestamp: Long,
        private val _id: Long
) : RoutineData(_id) {
    override fun hashCode(): Int = id.toInt()
    override fun equals(other: Any?): Boolean = if (other is DailyRoutineData) id == other.id else false
}

data class WeeklyRoutineData(
        val description: String,
        val days: Set<DayOfWeek>,
        val completedDays: Set<DayOfWeek>,
        val lastCompletedTimestamp: Long,
        private val _id: Long
) : RoutineData(_id) {
    override fun hashCode(): Int = id.toInt()
    override fun equals(other: Any?): Boolean = if (other is DailyRoutineData) id == other.id else false
}

data class RoutinesListData(
        val routines: Set<RoutineData>
)