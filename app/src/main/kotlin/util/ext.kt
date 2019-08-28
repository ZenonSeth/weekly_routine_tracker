package util

import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import application.RoutineApplication
import com.google.gson.Gson
import java.util.concurrent.atomic.AtomicBoolean

fun Fragment.getApplicationComponent() =
        (requireActivity().application as RoutineApplication).component()

fun <T> Bundle.putObjectJson(key: String, obj: T) =
    putString(key, Gson().toJson(obj))

fun <T> Bundle.getJsonObject(key: String, clazz: Class<T>): T? =
    runCatching { Gson().fromJson<T>(this.getString(key), clazz) }.getOrNull()

fun CheckBox.setCheckedIfDifferent(checked: Boolean) {
    if (isChecked != checked) { toggle() }
}

fun TextView.setTextIfDifferent(newText: String) {
    if (newText != text.toString()) {
        text = newText
    }
}

fun <T> MutableLiveData<T>.emit(newState: (currentState: T?) -> T) {
    value = newState(value)
}

/**
 *  Guards blocks from being executed at the same time<br>
 *  If the same guard is executing a block when you try to run a new block,
 *  it will simply not run the new block
 */
class ExecutionGuard {
    private var guard = AtomicBoolean(false)
    /**
     * Runs a block of code if and only if this Guard is in a Free state, otherwise does nothing<br>
     *     If it runs a block of code it will set the state to Not Free
     */
    fun runGuarding(block: () -> Unit) {
        if (!guard.getAndSet(true)) {
            block()
            guard.set(false)
        }
    }

    /**
     * Runs a block of code if this Guard is in a Free state, but won't change it's state
     */
    fun runIfFree(block: () -> Unit) {
        if (!guard.get()) {
            block()
        }
    }
}