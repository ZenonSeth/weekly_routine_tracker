package util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CoroutineDispatcherProvider(
    val main: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher
) {
    @Inject
    constructor() : this(Dispatchers.Main, Dispatchers.IO, Dispatchers.Default)
}