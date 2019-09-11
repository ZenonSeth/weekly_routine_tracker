package features.showroutines

import androidx.lifecycle.Lifecycle
import data.RoutineData
import data.RoutinesListData
import enums.RepeatType
import features.util.BaseModelTest
import features.util.TestObserver
import features.util.valueOrNull
import io.mockk.*
import mvi.Consumable
import org.junit.Before
import org.junit.Test

class ShowRoutinesModelTest : BaseModelTest() {

    private val model = ShowRoutinesModel()

    private val stateObserver = TestObserver<ShowRoutinesState>()
    private val eventObserver = TestObserver<Consumable<ShowRoutinesEvent>>()

    @Before
    fun setup() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        MockKAnnotations.init(this)
        mockBaseModel(model)
        model.saveRoutineMemoryToStorage = mockk(relaxUnitFun = true)
        model.loadRoutineStorageIntoMemory = mockk(relaxUnitFun = true)
        model.removeRoutineMemory = mockk(relaxUnitFun = true)
        model.getRoutinesMemory = mockk(relaxUnitFun = true)
        model.observe(lifecycleOwner, stateObserver, eventObserver)
    }

    @Test
    fun `routines loaded from disk on startup if memory is empty `() {
        every { model.getRoutinesMemory.invoke() }.answers { RoutinesListData() }
        model.postIntent(ShowRoutinesIntent.OnStartingUp)

        coVerify(exactly = 1) { model.loadRoutineStorageIntoMemory.invoke() }
    }

    @Test
    fun `routines are saved to disk when user leaving`() {
        model.postIntent(ShowRoutinesIntent.OnShuttingDown)

        coVerify(exactly = 1) { model.saveRoutineMemoryToStorage.invoke() }
    }

    @Test
    fun `routine list is passed for display on startup`() {
        val data = getRoutinesListData()
        every { model.getRoutinesMemory.invoke() }.answers { data }
        model.postIntent(ShowRoutinesIntent.OnStartingUp)

        check(stateObserver.values().size == 2)
        check(stateObserver.latest()?.routinesList == data)
    }

    @Test
    fun `call to go to add routine screen is made when add routine button clicked`() {
        model.postIntent(ShowRoutinesIntent.AddNewRoutine)
        check(eventObserver.latest()?.valueOrNull() == ShowRoutinesEvent.AddNewRoutine)
    }

    @Test
    fun `routine is deleted when long item is long pressed`() {
        val data = getRoutinesListData()
        val item = data.routines.toList()[0]
        every { model.getRoutinesMemory.invoke() }.answers { data }
        model.postIntent(ShowRoutinesIntent.OnItemLongClick(item))

        verify { model.removeRoutineMemory.invoke(item.id) }
    }

    @Test
    fun `call to edit routine when item is pressed`() {
        val data = getRoutinesListData()
        val item = data.routines.toList()[0]
        every { model.getRoutinesMemory.invoke() }.answers { data }
        model.postIntent(ShowRoutinesIntent.OnItemClick(item))

        check(eventObserver.latest()?.valueOrNull() == ShowRoutinesEvent.EditRoutine(item))
    }

    private fun getRoutinesListData() =
        RoutinesListData(setOf(RoutineData(1, "title one", RepeatType.Daily)))

}