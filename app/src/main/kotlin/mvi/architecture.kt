package mvi

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import util.CoroutineDispatcherProvider
import javax.inject.Inject

abstract class MviModel<VIEW_INTENT, VIEW_STATE, MODEL_EVENT> : ViewModel() {

    abstract fun getInitialState(): VIEW_STATE

    @Inject lateinit var dispatcher: CoroutineDispatcherProvider

    protected abstract fun handleIntent(intent: VIEW_INTENT)
    private val state = MutableLiveData<VIEW_STATE>()
    private val event = MutableLiveData<Event<MODEL_EVENT>>()
    protected val currentState
        get() = state.value ?: getInitialState()

    init {
        emitState { getInitialState() }
    }

    protected fun emitState(func: (VIEW_STATE) -> VIEW_STATE) {
        runOnMainIfNeeded { state.value = func(currentState) }
    }

    protected fun emitEvent(func: () -> MODEL_EVENT) {
        runOnMainIfNeeded { event.value = Event(func()) }
    }

    private fun runOnMainIfNeeded(block: () -> Unit) {
        if (ArchTaskExecutor.getInstance().isMainThread) {
            block()
        } else {
            runMain { block() }
        }
    }

    protected fun runMain(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(context = dispatcher.main, block = block)

    protected fun runComputation(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(context = dispatcher.computation, block = block)

    protected fun runIo(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(context = dispatcher.io, block = block)

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