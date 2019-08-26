package mvi

abstract class MviModel<in INTENT, VIEW_STATE> {
    abstract fun attachViewModel(viewModel: MviView<INTENT, VIEW_STATE>)
}