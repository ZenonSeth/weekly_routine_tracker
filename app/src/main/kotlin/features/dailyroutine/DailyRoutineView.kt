package features.dailyroutine

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import features.routinesadapter.RoutinesAdapter
import features.routinesadapter.RoutinesAdapterMode
import kotlinx.android.synthetic.main.daily_routine_layout.view.*
import mvi.MviView

class DailyRoutineView : MviView<DailyRoutineViewIntent, DailyRoutineViewState>() {

    private var fragment: DailyRoutineFragment? = null

    private lateinit var view: ViewGroup
    private var currentState: DailyRoutineViewState? = null

    private var observer = Observer<Pair<DailyRoutineViewIntent, DailyRoutineViewState>> {}

    fun init(fragment: DailyRoutineFragment) {
        this.fragment = fragment
        onFragmentSet()
    }

    fun sendIntent(intent: DailyRoutineViewIntent) {
        observer.onChanged(Pair(intent, currentState!!))
    }

    private fun onFragmentSet() {
        view = fragment!!.view as ViewGroup
        fragment?.lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                sendIntent(DailyRoutineViewIntent.OnShuttingDown)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                sendIntent(DailyRoutineViewIntent.OnStartingUp)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                sendIntent(DailyRoutineViewIntent.OnResuming)
            }
        })
        view.manage_routines_button.setOnClickListener { sendIntent(DailyRoutineViewIntent.ManageButtonClick) }


        render(DailyRoutineViewState.Initial)
    }

    override fun observeIntent(observer: Observer<Pair<DailyRoutineViewIntent, DailyRoutineViewState>>) {
        this.observer = observer
    }

    override fun render(state: DailyRoutineViewState) {
        if (state.manageRoutines) {
            fragment?.showAllRoutinesFragment()
        } else {
            view.daily_routines_rv.adapter =
                    RoutinesAdapter(fragment!!.context!!, state.routinesList.routines.toList(), RoutinesAdapterMode.DailyDisplay)
                            .also {
                                it.setOnItemClickListener { sendIntent(DailyRoutineViewIntent.ItemClicked(it)) }
                            }
        }
        currentState = state
    }
}