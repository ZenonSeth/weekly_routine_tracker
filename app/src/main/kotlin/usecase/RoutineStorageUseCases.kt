package usecase

import storage.InternalStorage
import javax.inject.Inject
import kotlin.concurrent.thread

const val ROUTINES_FILENAME = "routines.file"

class ReadRoutinesFromStorage @Inject constructor(private val storageAccess: InternalStorage) {
    operator fun invoke(): String =
            storageAccess.readFile(ROUTINES_FILENAME) ?: ""

}

class WriteRoutinesToStorage @Inject constructor(private val storageAccess: InternalStorage) {
    operator fun invoke(routinesString: String) =
            thread(start = true) {
                storageAccess.writeToFile(ROUTINES_FILENAME, routinesString)
            }
}

class ClearRoutinesStorage @Inject constructor(private val storageAccess: InternalStorage) {
    operator fun invoke() =
            thread(start = true) {
                storageAccess.removeFile(ROUTINES_FILENAME)
            }
}
