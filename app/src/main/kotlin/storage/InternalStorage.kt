package storage

import android.content.Context
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InternalStorage @Inject constructor(private val context: Context) {
    fun writeToFile(filename: String, content: String) =
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(content.toByteArray())
            }

    fun readFile(filename: String): String? {
        return try {
            context.openFileInput(filename).use {
                String(it.readBytes())
            }
        } catch (ignored: FileNotFoundException) {
            null
        }
    }

    fun removeFile(filename: String) =
        context.deleteFile(filename)

}