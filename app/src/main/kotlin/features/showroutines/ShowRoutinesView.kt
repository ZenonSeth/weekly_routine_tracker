package features.showroutines

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import features.routinesadapter.RoutinesAdapter
import features.routinesadapter.RoutinesAdapterMode
import kotlinx.android.synthetic.main.show_routines_layout.view.*
import mvi.MviView

class ShowRoutinesView : MviView<ShowRoutinesIntent, ShowRoutinesViewState>() {

    private var fragment: ShowRoutinesFragment? = null

    private var observer = Observer<Pair<ShowRoutinesIntent, ShowRoutinesViewState>> {}
    private var currentState: ShowRoutinesViewState? = null
    private lateinit var view: ViewGroup

    fun init(fragment: ShowRoutinesFragment) {
        this.fragment = fragment
        onFragmentSet()
    }

    fun sendIntent(intent: ShowRoutinesIntent) {
        observer.onChanged(Pair(intent, currentState!!))
    }

    private fun onFragmentSet() {
        view = fragment!!.view as ViewGroup
        fragment?.lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                sendIntent(ShowRoutinesIntent.OnShuttingDown)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                sendIntent(ShowRoutinesIntent.OnStartingUp)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                sendIntent(ShowRoutinesIntent.OnPausing)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                sendIntent(ShowRoutinesIntent.OnResuming)
            }
        })
        view.new_routine_button.setOnClickListener {
            sendIntent(ShowRoutinesIntent.AddNewRoutine)
        }
        render(ShowRoutinesViewState.Initial)
    }

    override fun observeIntent(observer: Observer<Pair<ShowRoutinesIntent, ShowRoutinesViewState>>) {
        this.observer = observer
    }

    override fun render(state: ShowRoutinesViewState) {
        when {
            state.addNewRoutine -> fragment?.showNewRoutineFragment()
            state.editRoutine != null -> fragment?.showNewRoutineFragment(state.editRoutine)
            else -> setupRecyclerView(state)

        }

        currentState = state
    }

    private fun setupRecyclerView(state: ShowRoutinesViewState) {
        view.all_routines_rv.adapter =
                RoutinesAdapter(fragment!!.context!!, state.routinesList.routines.toList(), RoutinesAdapterMode.AllDisplay)
                        .also {
                            it.setOnItemLongClickListener { sendIntent(ShowRoutinesIntent.OnItemLongClick(it)) }
                            it.setOnItemClickListener { sendIntent(ShowRoutinesIntent.OnItemClick(it)) }
                        }
    }

}