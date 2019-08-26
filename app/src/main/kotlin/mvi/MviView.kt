package mvi

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

abstract class MviView<INTENT, VIEW_STATE> : ViewModel() {
    abstract fun observeIntent(observer: Observer<Pair<INTENT, VIEW_STATE>>)
    abstract fun render(state: VIEW_STATE)
}