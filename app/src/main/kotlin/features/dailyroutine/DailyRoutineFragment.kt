package features.dailyroutine

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
import features.activity.IActionbarActivity
import features.activity.INavigationActivity
import features.routinesadapter.RoutinesAdapter
import features.routinesadapter.RoutinesAdapterMode
import features.showroutines.ShowRoutinesFragment
import kotlinx.android.synthetic.main.daily_routine_layout.view.*
import mvi.Consumable
import util.ExecutionGuard
import util.getApplicationComponent

class DailyRoutineFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    constructor() : this(0)

    lateinit var viewModel: DailyRoutineModel

    private val intentGuard = ExecutionGuard()
    private val renderer = Observer<DailyRoutineState> { render(it) }
    private val eventHandler = Observer<Consumable<DailyRoutineEvent>> { it.consume { handle(it) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DailyRoutineModel::class.java)
        getApplicationComponent().inject(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.daily_routine_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.daily_routines_rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.observe(this, renderer, eventHandler)
        initIntentListeners()
    }

    private fun initIntentListeners() {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                sendIntent(DailyRoutineIntent.OnShuttingDown)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                sendIntent(DailyRoutineIntent.OnStartingUp)
            }
        })
        view?.manage_routines_button?.setOnClickListener { sendIntent(DailyRoutineIntent.ManageButtonClick) }
    }

    override fun onResume() {
        super.onResume()
        (context as? IActionbarActivity)?.setActionbarTitle(resources.getString(R.string.daily_routine_title))
    }

    fun sendIntent(intent: DailyRoutineIntent) =
        intentGuard.runIfFree { viewModel.postIntent(intent) }

    private fun render(state: DailyRoutineState) = intentGuard.runGuarding {
        view?.daily_routines_rv?.adapter =
            RoutinesAdapter(
                requireContext(),
                state.routinesList.routines.toList(),
                RoutinesAdapterMode.DailyDisplay)
                .also {
                    it.setOnItemClickListener { sendIntent(DailyRoutineIntent.ItemClicked(it)) }
                }
    }

    private fun handle(it: DailyRoutineEvent) {
        when (it) {
            DailyRoutineEvent.GoToManageRoutineScreen -> showAllRoutinesFragment()
        }
    }

    fun showAllRoutinesFragment() {
        (context as? INavigationActivity)
            ?.addFragment(ShowRoutinesFragment(), ShowRoutinesFragment::class.java.simpleName)
    }
}