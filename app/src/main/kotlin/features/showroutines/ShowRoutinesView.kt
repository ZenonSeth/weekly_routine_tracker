package features.showroutines

import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.*
import features.addroutine.AddRoutineIntent
import features.routinesadapter.RoutinesAdapter
import kotlinx.android.synthetic.main.show_routines_layout.view.*
import mvi.MviView

class ShowRoutinesView : MviView<ShowRoutinesIntent, ShowRoutinesViewState>() {

    private var fragment: ShowRoutinesFragment? = null

    private val intentData by lazy { MutableLiveData<Pair<ShowRoutinesIntent, ShowRoutinesViewState>>() }
    private var currentState: ShowRoutinesViewState? = null
    private lateinit var view: ViewGroup

    fun init(fragment: ShowRoutinesFragment) {
        this.fragment = fragment
        onFragmentSet()
    }

    fun sendIntent(intent: ShowRoutinesIntent) {
        intentData.value = Pair(intent, currentState!!)
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
        intentData.observe(fragment!!, observer)
    }

    override fun render(state: ShowRoutinesViewState) {
        if (state.addNewItent) {
            fragment?.showNewRoutineFragment()
        } else {
            setupRecyclerView(state)
        }

        currentState = state
    }

    private fun setupRecyclerView(state: ShowRoutinesViewState) {
        Log.d("MIPE", "MIPE: setting recycler view with state list = ${state.routinesList.routines.size}")
        view.all_routines_rv.adapter = RoutinesAdapter(fragment!!.context!!, state.routinesList.routines.toList())
    }

}