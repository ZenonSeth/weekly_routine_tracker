package features.showroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.milchopenchev.weeklyexercisetracker.R
import data.RoutineData
import features.activity.IActionbarActivity
import features.activity.INavigationActivity
import features.addroutine.AddRoutineFragment
import features.routinesadapter.RoutinesAdapter
import features.routinesadapter.RoutinesAdapterMode
import kotlinx.android.synthetic.main.show_routines_layout.view.*
import mvi.Consumable
import util.ExecutionGuard
import util.getApplicationComponent
import util.putObjectJson

class ShowRoutinesFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    constructor() : this(0)

    lateinit var viewModel: ShowRoutinesModel

    private val renderer = Observer<ShowRoutinesState> { render(it) }
    private val eventHandler = Observer<Consumable<ShowRoutinesEvent>> { it.consume { handle(it) } }
    private val intentGuard = ExecutionGuard()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ShowRoutinesModel::class.java)
        getApplicationComponent().inject(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.show_routines_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.all_routines_rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.observe(this, renderer, eventHandler)
        initIntentListeners()
    }

    private fun initIntentListeners() {
        lifecycle.addObserver(object : LifecycleObserver {
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
        })
        view?.new_routine_button?.setOnClickListener {
            sendIntent(ShowRoutinesIntent.AddNewRoutine)
        }
    }

    override fun onResume() {
        super.onResume()
        (context as? IActionbarActivity)?.setActionbarTitle(resources.getString(R.string.show_routines_title))
    }

    fun sendIntent(intent: ShowRoutinesIntent) =
        intentGuard.runIfFree { viewModel.postIntent(intent) }

    private fun handle(it: ShowRoutinesEvent) {
        when (it) {
            ShowRoutinesEvent.AddNewRoutine -> showNewRoutineFragment(null)
            is ShowRoutinesEvent.EditRoutine -> showNewRoutineFragment(it.data)
        }
    }

    private fun render(state: ShowRoutinesState) = intentGuard.runGuarding {
        view?.all_routines_rv?.adapter =
            RoutinesAdapter(
                requireContext(),
                state.routinesList.routines.toList(),
                RoutinesAdapterMode.AllDisplay)
                .also {
                    it.setOnItemLongClickListener { sendIntent(ShowRoutinesIntent.OnItemLongClick(it)) }
                    it.setOnItemClickListener { sendIntent(ShowRoutinesIntent.OnItemClick(it)) }
                }
    }

    private fun showNewRoutineFragment(routine: RoutineData? = null) {
        (context as? INavigationActivity)
            ?.addFragment(getRoutineFragment(routine), AddRoutineFragment::class.java.simpleName)
    }

    private fun getRoutineFragment(routine: RoutineData?): Fragment =
        AddRoutineFragment().also { fragment ->
            routine?.let {
                fragment.arguments = Bundle().also { it.putObjectJson(AddRoutineFragment.ROUTINE_DATA, routine) }
            }
        }
}
