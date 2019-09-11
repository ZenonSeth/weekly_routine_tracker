package features.addroutine

import androidx.lifecycle.Lifecycle
import data.RoutineData
import enums.DayOfWeek
import enums.RepeatType
import features.util.BaseModelTest
import features.util.TestObserver
import features.util.valueOrNull
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import mvi.Consumable
import org.junit.Before
import org.junit.Test

class AddRoutineModelTest : BaseModelTest() {

    private val model = AddRoutineModel()

    private val stateObserver = TestObserver<AddRoutineState>()
    private val eventObserver = TestObserver<Consumable<AddRoutineEvent>>()

    @Before
    fun setup() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        MockKAnnotations.init(this)
        mockBaseModel(model)
        model.putRoutineMemory = mockk(relaxUnitFun = true)
        model.saveRoutineMemoryToStorage = mockk(relaxUnitFun = true)
        model.observe(lifecycleOwner, stateObserver, eventObserver)
    }

    @Test
    fun `finished is called and no saving is done when cancel is clicked`() {
        model.postIntent(AddRoutineIntent.CancelledClicked)

        verify(exactly = 0) { model.putRoutineMemory.invoke(any()) }
        coVerify(exactly = 0) { model.saveRoutineMemoryToStorage.invoke() }
        check(eventObserver.values().size == 1)
        check(eventObserver.latest()?.valueOrNull() is AddRoutineEvent.Finish)
    }

    @Test
    fun `routine is added to memory and finished is called when save is clicked`() {
        model.postIntent(AddRoutineIntent.SaveClicked)

        verify(exactly = 1) { model.putRoutineMemory.invoke(any()) }
        check(eventObserver.values().size == 1)
        check(eventObserver.latest()?.valueOrNull() is AddRoutineEvent.Finish)
    }

    @Test
    fun `routines are saved when user exits`() {
        model.postIntent(AddRoutineIntent.OnUserLeaving)

        coVerify(exactly = 1) { model.saveRoutineMemoryToStorage.invoke() }
    }

    @Test
    fun `save button is disabled at start`() {
        check(stateObserver.values().size == 1)
        check(stateObserver.latest()?.saveEnabled == false)
    }

    @Test
    fun `save button gets enabled when daily routine with title is entered`() {
        model.postIntent(AddRoutineIntent.RepeatTypeChanged(RepeatType.Daily))
        check(stateObserver.latest()?.saveEnabled == false)
        model.postIntent(AddRoutineIntent.TitleChanged("test"))
        check(stateObserver.latest()?.saveEnabled == true)
    }

    @Test
    fun `save button is not enabled when weekly routine with only title is entered`() {
        model.postIntent(AddRoutineIntent.RepeatTypeChanged(RepeatType.Weekly))
        model.postIntent(AddRoutineIntent.TitleChanged("test"))
        check(stateObserver.latest()?.saveEnabled == false)
    }

    @Test
    fun `save button is enabled when weekly routine with title and days is entered`() {
        model.postIntent(AddRoutineIntent.RepeatTypeChanged(RepeatType.Weekly))
        check(stateObserver.latest()?.saveEnabled == false)
        model.postIntent(AddRoutineIntent.TitleChanged("test"))
        check(stateObserver.latest()?.saveEnabled == false)
        model.postIntent(AddRoutineIntent.DayCheckedChange(DayOfWeek.Mon, true))
        check(stateObserver.latest()?.saveEnabled == true)
    }

    @Test
    fun `values are pre-populated when initial data is supplied`() {
        val routineData = RoutineData(
            id = 1,
            description = "title",
            type = RepeatType.Weekly,
            days = setOf(DayOfWeek.Tue, DayOfWeek.Wed)
        )
        model.postIntent(AddRoutineIntent.PresetData(routineData))

        val latestState = stateObserver.latest()
        check(latestState != null)
        check(latestState.saveEnabled)
        check(latestState.daysVisible)
        check(latestState.daysSelected.containsAll(routineData.days))
        check(routineData.days.containsAll(latestState.daysSelected))
        check(latestState.title == routineData.description)
        check(latestState.repeatType == routineData.type)
    }
}