package features.addroutine

import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.*
import data.RoutineData
import enums.DayOfWeek.*
import enums.RepeatType
import kotlinx.android.synthetic.main.add_routine_layout.view.*
import mvi.MviView

class AddRoutineView
    : MviView<AddRoutineIntent, AddRoutineViewState>() {

    private var fragment: AddRoutineFragment? = null
    private var routineData: RoutineData? = null

    private val view: ViewGroup by lazy { fragment?.view as ViewGroup }
    private var currentState: AddRoutineViewState? = null

    private val intentData by lazy { MutableLiveData<AddRoutineIntent>() }

    fun init(fragment: AddRoutineFragment, routineData: RoutineData? = null) {
        this.fragment = fragment
        this.routineData = routineData
        onFragmentSet()
    }

    private fun onFragmentSet() {
        fragment?.lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                intentData.value = AddRoutineIntent.OnUserLeaving
            }
        })
        view.daily_radio_button.setOnClickListener {
            intentData.value = AddRoutineIntent.RepeatTypeChanged(RepeatType.Daily)
        }
        view.weekly_radio_button.setOnClickListener {
            intentData.value = AddRoutineIntent.RepeatTypeChanged(RepeatType.Weekly)
        }
        view.save_button.setOnClickListener {
            intentData.value = AddRoutineIntent.SaveClicked
        }
        view.cancel_button.setOnClickListener {
            intentData.value = AddRoutineIntent.CancelledClicked
        }
        view.title_edit_text.addTextChangedListener {
            intentData.value = (AddRoutineIntent.TitleChanged(it?.toString() ?: ""))
        }
        render(AddRoutineViewState.Initial)
    }

    override fun observeIntent(observer: (AddRoutineIntent, AddRoutineViewState) -> Unit) {
        intentData.observe(fragment!!, Observer { observer(it, currentState!!) })
    }

    override fun render(state: AddRoutineViewState) {
        when {
            state.finished -> fragment?.finished()
            else -> {
                if (state.title != view.title_edit_text.text.toString()) {
                    view.title_edit_text.setText(state.title)
                }
                if (state.repeatType != currentState?.repeatType) {
                    when (state.repeatType) {
                        RepeatType.Daily -> view.daily_radio_button.isChecked = true
                        RepeatType.Weekly -> view.weekly_radio_button.isChecked = true
                    }
                }

                if (state.daysVisible != currentState?.daysVisible) {
                    view.day_selection_group.visibility = if (state.daysVisible) View.VISIBLE else View.GONE
                }
                if (!(state.daysSelected.containsAll(currentState?.daysSelected ?: emptySet())
                                && currentState?.daysSelected?.containsAll(state.daysSelected) ?: true)) {
                    state.daysSelected.forEach {
                        when (it) {
                            Mon -> view.mon_cb.isChecked = true
                            Tue -> view.tue_cb.isChecked = true
                            Wed -> view.wed_cb.isChecked = true
                            Thu -> view.thu_cb.isChecked = true
                            Fri -> view.fri_cb.isChecked = true
                            Sat -> view.sat_cb.isChecked = true
                            Sun -> view.sun_cb.isChecked = true
                        }
                    }
                }
                if (state.saveEnabled != currentState?.saveEnabled) {
                    view.save_button.isEnabled = state.saveEnabled
                }
            }
        }
        currentState = state
    }
}