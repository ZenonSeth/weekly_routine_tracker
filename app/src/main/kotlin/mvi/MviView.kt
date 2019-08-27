package mvi

interface MviView<INTENT, VIEW_STATE> {
    fun render(state: VIEW_STATE)
}