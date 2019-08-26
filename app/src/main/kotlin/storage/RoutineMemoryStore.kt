package storage

import data.RoutineData
import data.RoutinesListData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineMemoryStore @Inject constructor() {
    private var data: RoutinesListData = RoutinesListData(emptySet())

    fun getRoutinesData() = data

    fun putRoutine(routineData: RoutineData) {
        removeRoutine(routineData.id)
        data = data.copy(routines = data.routines.plus(routineData))
    }

    fun removeRoutine(id: Long) =
            data.routines
                    .firstOrNull { it.id == id }
                    ?.let { data = data.copy(routines = data.routines.minus(it)) }
                    ?: Unit
}