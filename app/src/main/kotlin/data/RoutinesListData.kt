package data

import enums.DayOfWeek
import enums.RepeatType

data class RoutineData(
        val id: Long,
        val description: String,
        val type: RepeatType,
        val completed: Boolean = false,
        val days: Set<DayOfWeek> = emptySet(),
        val completedDays: Set<DayOfWeek> = emptySet(),
        val lastCompletedTimestamp: Long = 0L) {
    constructor() : this(0, "", RepeatType.Daily)

    override fun hashCode(): Int = id.toInt()
    override fun equals(other: Any?): Boolean = if (other is RoutineData) id == other.id else false
}

data class RoutinesListData(val routines: Set<RoutineData>) {
    constructor() : this(emptySet())
}