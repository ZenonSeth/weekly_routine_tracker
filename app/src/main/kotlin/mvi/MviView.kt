package mvi

import androidx.lifecycle.ViewModel

abstract class MviView<out INTENT, VIEW_STATE> : ViewModel() {
    abstract fun observeIntent(observer: (INTENT, VIEW_STATE) -> Unit)
    abstract fun render(state: VIEW_STATE)
}