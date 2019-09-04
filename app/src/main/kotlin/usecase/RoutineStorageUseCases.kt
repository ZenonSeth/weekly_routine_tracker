package usecase

import data.RoutinesListData
import storage.InternalStorage
import javax.inject.Inject
import kotlin.concurrent.thread

const val ROUTINES_FILENAME = "routines.file"

class ReadRoutinesStorage @Inject constructor(
    private val readRoutinesFile: ReadRoutinesFile,
    private val convertJsonToRoutinesList: ConvertJsonToRoutinesList) {
    operator fun invoke(): RoutinesListData {
        return readRoutinesFile().let { convertJsonToRoutinesList(it) }
    }
}

class SaveRoutinesStorage @Inject constructor(
    private val writeRoutinesFile: WriteRoutinesFile,
    private val convertRoutinesListToJson: ConvertRoutinesListToJson) {
    operator fun invoke(routines: RoutinesListData) {
        writeRoutinesFile(convertRoutinesListToJson(routines))
    }
}

class SaveRoutineMemoryToStorage @Inject constructor(
    private val getRoutinesMemory: GetRoutinesMemory,
    private val saveRoutinesStorage: SaveRoutinesStorage) {
    operator fun invoke() {
        saveRoutinesStorage(getRoutinesMemory())
    }
}

class LoadRoutineStorageIntoMemory @Inject constructor(
    private val setRoutinesMemory: SetRoutinesMemory,
    private val readRoutinesStorage: ReadRoutinesStorage) {
    operator fun invoke() {
        setRoutinesMemory(readRoutinesStorage())
    }
}

class ReadRoutinesFile @Inject constructor(private val storageAccess: InternalStorage) {
    operator fun invoke(): String =
        storageAccess.readFile(ROUTINES_FILENAME) ?: ""

}

class WriteRoutinesFile @Inject constructor(private val storageAccess: InternalStorage) {
    operator fun invoke(routinesString: String) =
        storageAccess.writeToFile(ROUTINES_FILENAME, routinesString)
}

class ClearRoutinesStorage @Inject constructor(private val storageAccess: InternalStorage) {
    operator fun invoke() =
        thread(start = true) {
            storageAccess.removeFile(ROUTINES_FILENAME)
        }
}
