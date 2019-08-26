package usecase

import data.RoutineData
import data.RoutinesListData
import storage.RoutineMemoryStore
import javax.inject.Inject

class PutRoutineMemory @Inject constructor(private val store: RoutineMemoryStore) {
    operator fun invoke(routine: RoutineData): Unit =
            store.putRoutine(routine)
}

class GetRoutinesMemory @Inject constructor(private val store: RoutineMemoryStore) {
    operator fun invoke(): RoutinesListData =
            store.getRoutinesData()
}

class RemoveRoutineMemory @Inject constructor(private val store: RoutineMemoryStore) {
    operator fun invoke(routineId: Long): Unit =
            store.removeRoutine(routineId)
}

class ClearRoutionesMemory @Inject constructor(private val store: RoutineMemoryStore) {
    operator fun invoke() = store.clear()
}

class SetRoutinesMemory @Inject constructor(private val store: RoutineMemoryStore) {
    operator fun invoke(routineData: RoutinesListData) = store.set(routineData)
}