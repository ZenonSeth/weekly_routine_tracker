package features.dailyroutine

import androidx.lifecycle.Lifecycle
import data.RoutineData
import data.RoutinesListData
import enums.DayOfWeek
import enums.RepeatType
import features.util.BaseModelTest
import features.util.TestObserver
import features.util.valueOrNull
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import mvi.Consumable
import org.junit.Before
import org.junit.Test

class DailyRoutineModelTest : BaseModelTest() {

    private val model = DailyRoutineModel()

    private val stateObserver = TestObserver<DailyRoutineState>()
    private val eventObserver = TestObserver<Consumable<DailyRoutineEvent>>()

    @Before
    fun setup() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        MockKAnnotations.init(this)
        mockBaseModel(model)
        model.saveRoutineMemoryToStorage = mockk(relaxUnitFun = true)
        model.loadRoutineStorageIntoMemory = mockk(relaxUnitFun = true)
        model.putRoutineMemory = mockk(relaxUnitFun = true)
        model.getRoutinesMemory = mockk(relaxUnitFun = true)
        model.completeRoutineToggle = mockk(relaxUnitFun = true)
        model.filterRoutines = mockk(relaxUnitFun = true)
        model.resetRoutinesInMemory = mockk(relaxUnitFun = true)

        every { model.completeRoutineToggle.invoke(any(), any()) }
            .answers { it.invocation.args[0] as RoutineData }

        every { model.getRoutinesMemory.invoke() }
            .answers { RoutinesListData() }

        every { model.filterRoutines.invoke(any(), any()) }
            .answers { it.invocation.args[0] as RoutinesListData }

        model.observe(lifecycleOwner, stateObserver, eventObserver)
    }

    @Test
    fun `routines are loaded from disk if memory is empty on startup`() {
        model.postIntent(DailyRoutineIntent.OnStartingUp)
        verify(exactly = 1) { runBlocking { model.loadRoutineStorageIntoMemory.invoke() } }
    }

    @Test
    fun `routines reset in memory when starting up`() {
        model.postIntent(DailyRoutineIntent.OnStartingUp)
        verify(exactly = 1) { runBlocking { model.resetRoutinesInMemory.invoke(any()) } }
    }

    @Test
    fun `filtered routines are displayed when memory is popuated on starting up`() {
        val data = getRoutinesListData()
        val item = data.routines.toList()[0]
        every { model.getRoutinesMemory.invoke() }.answers { data }
        every { model.filterRoutines.invoke(any(), any()) }
            .answers{
                RoutinesListData((invocation.args[0] as RoutinesListData).routines.minus(item))
            }

        model.postIntent(DailyRoutineIntent.OnStartingUp)

        check(stateObserver.values().size == 2)
        val lastState = stateObserver.latest()
        check(lastState != null)
        check(lastState.routinesList.routines.size == 1)
    }

    @Test
    fun `routines reset in memory and written to disk when shutting down`() {
        model.postIntent(DailyRoutineIntent.OnShuttingDown)
        verify(exactly = 1) { runBlocking { model.resetRoutinesInMemory.invoke(any()) } }
        verify(exactly = 1) { runBlocking { model.saveRoutineMemoryToStorage.invoke() } }
    }

    @Test
    fun `routine toggled completed and new data posted when item clicked`() {
        val data = getRoutinesListData()
        val item = data.routines.toList()[0]
        every { model.getRoutinesMemory.invoke() }.answers { data }
        model.postIntent(DailyRoutineIntent.ItemClicked(item))
        verify { model.completeRoutineToggle.invoke(item, any()) }
        check(stateObserver.values().size == 2)
    }

    @Test
    fun `go to all routines screen when manage button clicked`() {
        model.postIntent(DailyRoutineIntent.ManageButtonClick)
        check(eventObserver.latest()?.valueOrNull() == DailyRoutineEvent.GoToManageRoutineScreen)
    }

    private fun getRoutinesListData() =
        RoutinesListData(setOf(
            RoutineData(1, "title one", RepeatType.Daily),
            RoutineData(2, "title two", RepeatType.Weekly, false, setOf(DayOfWeek.Mon))
        ))

}