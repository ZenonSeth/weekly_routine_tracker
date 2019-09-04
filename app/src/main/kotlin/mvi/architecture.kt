package mvi

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

abstract class MviModel<VIEW_INTENT, VIEW_STATE, MODEL_EVENT> : ViewModel() {

    abstract fun getInitialState(): VIEW_STATE

    protected abstract fun handleIntent(intent: VIEW_INTENT)
    private val state = MutableLiveData<VIEW_STATE>()
    private val event = MutableLiveData<Event<MODEL_EVENT>>()
    protected val currentState
        get() = state.value ?: getInitialState()

    init {
        emitState { getInitialState() }
    }

    protected fun emitState(func: (VIEW_STATE) -> VIEW_STATE) {
        state.value = func(currentState)
    }

    protected fun emitEvent(func: () -> MODEL_EVENT) {
        event.value = Event(func())
    }

    fun postIntent(intent: VIEW_INTENT) = handleIntent(intent)
    fun observe(lifecycleOwner: LifecycleOwner,
                stateObserver: Observer<VIEW_STATE>,
                eventObserver: Observer<Event<MODEL_EVENT>>) {
        state.observe(lifecycleOwner, stateObserver)
        event.observe(lifecycleOwner, eventObserver)
    }
}

class Event<T>(private val value: T) {
    private var used = false
    fun get(): T? = if (used) null else value.also { used = true }
}