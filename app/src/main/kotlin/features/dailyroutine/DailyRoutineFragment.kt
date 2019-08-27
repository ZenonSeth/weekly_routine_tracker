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
import mvi.MviView
import util.getApplicationComponent
import javax.inject.Inject

class DailyRoutineFragment(@LayoutRes contentLayoutId: Int)
    : Fragment(contentLayoutId),
    MviView<DailyRoutineViewIntent, DailyRoutineViewState> {
    constructor() : this(0)

    @Inject
    lateinit var mviModel: DailyRoutineModel
    private var observer = Observer<Pair<DailyRoutineViewIntent, DailyRoutineViewState>> {}

    private lateinit var viewModel: DailyRoutineAndroidViewModel
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getApplicationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.daily_routine_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        view.daily_routines_rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel = ViewModelProviders.of(this).get(DailyRoutineAndroidViewModel::class.java)
        mviModel.attachViewModel(this)
        initIntentListeners()
        render(DailyRoutineViewState.Initial)
    }

    fun initIntentListeners() {
        lifecycle.addObserver(object : LifecycleObserver {
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
        mView.manage_routines_button.setOnClickListener { sendIntent(DailyRoutineViewIntent.ManageButtonClick) }
    }

    override fun onResume() {
        super.onResume()
        (context as? IActionbarActivity)?.setActionbarTitle(resources.getString(R.string.daily_routine_title))
    }

    fun sendIntent(intent: DailyRoutineViewIntent) {
        observer.onChanged(Pair(intent, viewModel.currentState!!))
    }

    override fun render(state: DailyRoutineViewState) {
        if (state.manageRoutines) {
            showAllRoutinesFragment()
        } else {
            mView.daily_routines_rv.adapter =
                RoutinesAdapter(
                    requireContext(),
                    state.routinesList.routines.toList(),
                    RoutinesAdapterMode.DailyDisplay)
                    .also {
                        it.setOnItemClickListener { sendIntent(DailyRoutineViewIntent.ItemClicked(it)) }
                    }
        }
        viewModel.currentState = state
    }

    fun showAllRoutinesFragment() {
        (context as? INavigationActivity)
                ?.addFragment(ShowRoutinesFragment(), ShowRoutinesFragment::class.java.simpleName)
    }
}