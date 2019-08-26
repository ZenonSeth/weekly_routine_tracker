package storage

import android.content.Context
import java.io.FileNotFoundException

class InternalStorage(private val context: Context) {
    public fun writeToFile(filename: String, content: String) =
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(content.toByteArray())
            }

    public fun readFile(filename: String): String? {
        return try {
            context.openFileInput(filename).use {
                String(it.readBytes())
            }
        } catch (ignored: FileNotFoundException) {
            null
        }
    }
}