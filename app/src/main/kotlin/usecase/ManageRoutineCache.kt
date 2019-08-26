package usecase

import data.RoutineData
import data.RoutinesListData
import storage.RoutineMemoryStore
import javax.inject.Inject

class PutRoutine @Inject constructor(private val store: RoutineMemoryStore) {
    operator fun invoke(routine: RoutineData): Unit =
            store.putRoutine(routine)
}

class GetRoutines @Inject constructor(private val store: RoutineMemoryStore) {
    operator fun invoke(): RoutinesListData =
            store.getRoutinesData()
}

class RemoveRoutine @Inject constructor(private val store: RoutineMemoryStore) {
    operator fun invoke(routineId: Long): Unit =
            store.removeRoutine(routineId)
}

