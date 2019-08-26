package mvi

abstract class MviModel<INTENT, VIEW_STATE> {
    abstract fun attachViewModel(viewModel: MviView<INTENT, VIEW_STATE>)
}