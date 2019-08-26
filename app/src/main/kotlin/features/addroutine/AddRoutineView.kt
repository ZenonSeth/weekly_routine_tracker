package features.addroutine

import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.*
import data.RoutineData
import enums.DayOfWeek
import enums.RepeatType
import kotlinx.android.synthetic.main.add_routine_layout.view.*
import mvi.MviView

class AddRoutineView : MviView<AddRoutineIntent, AddRoutineViewState>() {

    private var fragment: AddRoutineFragment? = null
    private var routineData: RoutineData? = null

    private lateinit var view: ViewGroup
    private var currentState: AddRoutineViewState? = null

    private var observer = Observer<Pair<AddRoutineIntent, AddRoutineViewState>>{}

    fun init(fragment: AddRoutineFragment, routineData: RoutineData? = null) {
        this.fragment = fragment
        this.routineData = routineData
        onFragmentSet()
    }

    fun sendIntent(intent: AddRoutineIntent) {
        observer.onChanged(Pair(intent, currentState!!))
    }

    private fun onFragmentSet() {
        view = fragment!!.view as ViewGroup
        fragment?.lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                sendIntent(AddRoutineIntent.OnUserLeaving)
            }
        })
        view.daily_radio_button.setOnClickListener {
            sendIntent(AddRoutineIntent.RepeatTypeChanged(RepeatType.Daily))
        }
        view.weekly_radio_button.setOnClickListener {
            sendIntent(AddRoutineIntent.RepeatTypeChanged(RepeatType.Weekly))
        }
        view.save_button.setOnClickListener {
            sendIntent(AddRoutineIntent.SaveClicked)
        }
        view.cancel_button.setOnClickListener {
            sendIntent(AddRoutineIntent.CancelledClicked)
        }
        view.title_edit_text.addTextChangedListener {
            sendIntent(AddRoutineIntent.TitleChanged(it?.toString() ?: ""))
        }
        view.mon_cb.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Mon, isChecked) }
        view.tue_cb.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Tue, isChecked) }
        view.wed_cb.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Wed, isChecked) }
        view.thu_cb.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Thu, isChecked) }
        view.fri_cb.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Fri, isChecked) }
        view.sat_cb.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Sat, isChecked) }
        view.sun_cb.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Sun, isChecked) }
        render(AddRoutineViewState.Initial)
    }

    private fun onCheckChange(dayOfWeek: DayOfWeek, checked: Boolean) {
        sendIntent(AddRoutineIntent.DayCheckedChange(dayOfWeek, checked))
    }

    override fun observeIntent(observer: Observer<Pair<AddRoutineIntent, AddRoutineViewState>>) {
        this.observer = observer
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
                if (state.saveEnabled != currentState?.saveEnabled) {
                    view.save_button.isEnabled = state.saveEnabled
                }
            }
        }
        currentState = state
    }
}