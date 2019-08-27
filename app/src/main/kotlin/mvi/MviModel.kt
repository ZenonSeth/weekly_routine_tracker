package mvi

import androidx.lifecycle.LiveData

abstract class MviModel<INTENT, VIEW_STATE> {
    fun postIntent(intent: INTENT, currentState: VIEW_STATE?) =
            handleIntent(intent, currentState)

    protected abstract fun handleIntent(intent: INTENT, currentState: VIEW_STATE?)
    abstract val stateData: LiveData<VIEW_STATE>
}