package usecase

import data.RoutinesListData
import storage.InternalStorage
import javax.inject.Inject

const val ROUTINES_FILENAME = "routines.file"

class ReadRoutinesStorage @Inject constructor(
    private val readRoutinesFile: ReadRoutinesFile,
    private val convertJsonToRoutinesList: ConvertJsonToRoutinesList) {
    suspend operator fun invoke(): RoutinesListData {
        return convertJsonToRoutinesList(readRoutinesFile())
    }
}

class SaveRoutinesStorage @Inject constructor(
    private val writeRoutinesFile: WriteRoutinesFile,
    private val convertRoutinesListToJson: ConvertRoutinesListToJson) {
    suspend operator fun invoke(routines: RoutinesListData) {
        writeRoutinesFile(convertRoutinesListToJson(routines))
    }
}

class SaveRoutineMemoryToStorage @Inject constructor(
    private val getRoutinesMemory: GetRoutinesMemory,
    private val saveRoutinesStorage: SaveRoutinesStorage) {
    suspend operator fun invoke() {
        saveRoutinesStorage(getRoutinesMemory())
    }
}

class LoadRoutineStorageIntoMemory @Inject constructor(
    private val setRoutinesMemory: SetRoutinesMemory,
    private val readRoutinesStorage: ReadRoutinesStorage) {
    suspend operator fun invoke() {
        setRoutinesMemory(readRoutinesStorage())
    }
}

class ReadRoutinesFile @Inject constructor(private val storageAccess: InternalStorage) {
    suspend operator fun invoke(): String =
        storageAccess.readFile(ROUTINES_FILENAME) ?: ""

}

class WriteRoutinesFile @Inject constructor(private val storageAccess: InternalStorage) {
    suspend operator fun invoke(routinesString: String) =
        storageAccess.writeToFile(ROUTINES_FILENAME, routinesString)
}

class ClearRoutinesStorage @Inject constructor(private val storageAccess: InternalStorage) {
    suspend operator fun invoke() =
        storageAccess.removeFile(ROUTINES_FILENAME)
}
