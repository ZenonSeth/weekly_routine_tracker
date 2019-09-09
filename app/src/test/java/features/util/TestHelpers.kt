package features.util

import androidx.lifecycle.Observer
import mvi.Consumable
import java.util.*

class TestObserver<T> : Observer<T> {
    private val values = LinkedList<T>()
    override fun onChanged(value: T) {
        values.add(value)
    }

    fun values(): List<T> = values
    fun latest(): T? = if (values.isEmpty()) null else values[values.size - 1]
}

fun <T> Consumable<T>.valueOrNull(): T? {
    var ret: T? = null
    this.consume { ret = it }
    return ret
}