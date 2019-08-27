package mvi

import androidx.lifecycle.LiveData

interface MviModel<INTENT, VIEW_STATE> {
    fun handleIntent(intent: INTENT, currentState: VIEW_STATE?)
    val stateData: LiveData<VIEW_STATE>
}