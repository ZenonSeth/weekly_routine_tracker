package util

import android.os.Bundle
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import application.RoutineApplication
import com.google.gson.Gson

fun Fragment.getApplicationComponent() =
        (requireActivity().application as RoutineApplication).component()

fun <T> Bundle.putObjectJson(key: String, obj: T) =
    putString(key, Gson().toJson(obj))

fun <T> Bundle.getJsonObject(key: String, clazz: Class<T>): T? =
    runCatching { Gson().fromJson<T>(this.getString(key), clazz) }.getOrNull()

fun CheckBox.setCheckedIfDifferent(checked: Boolean) {
    if (isChecked != checked) { toggle() }
}

fun <T> MutableLiveData<T>.emit(newState: (currentState: T?) -> T) {
    value = newState(value)
}