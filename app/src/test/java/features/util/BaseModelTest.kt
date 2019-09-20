package features.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import mvi.MviModel
import org.junit.Rule
import util.CoroutineDispatcherProvider

abstract class BaseModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    protected val lifecycleOwner = mockk<LifecycleOwner>()
    protected val lifecycleRegistry = LifecycleRegistry(lifecycleOwner)

    protected fun mockBaseModel(model: MviModel<*, *, *>) {
        every { lifecycleOwner.lifecycle }.answers { lifecycleRegistry }
        model.dispatcher =
            CoroutineDispatcherProvider(Dispatchers.Unconfined, Dispatchers.Unconfined, Dispatchers.Unconfined)
    }
}