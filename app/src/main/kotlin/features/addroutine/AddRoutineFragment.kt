package features.addroutine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProviders
import com.milchopenchev.weeklyexercisetracker.R
import data.RoutineData
import enums.DayOfWeek
import enums.RepeatType
import features.activity.IActionbarActivity
import features.activity.INavigationActivity
import kotlinx.android.synthetic.main.add_routine_layout.view.*
import mvi.MviView
import util.ExecutionGuard
import util.getApplicationComponent
import util.getJsonObject
import util.setCheckedIfDifferent
import util.setTextIfDifferent
import javax.inject.Inject

class AddRoutineFragment(@LayoutRes contentLayoutId: Int)
    : Fragment(contentLayoutId),
        MviView<AddRoutineIntent, AddRoutineViewState> {
    constructor() : this(0)

    companion object {
        const val ROUTINE_DATA = "routine_data"
    }

    @Inject
    lateinit var mviModel: AddRoutineModel
    private lateinit var viewModel: AddRoutineAndroidViewModel
    private lateinit var mView: View

    private val renderer = Observer<AddRoutineViewState> { intentGuard.runGuarding { render(it) } }
    private val intentGuard = ExecutionGuard()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getApplicationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.add_routine_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        viewModel = ViewModelProviders.of(this).get(AddRoutineAndroidViewModel::class.java)
        mviModel.stateData.observe(this, renderer)
        initIntentListeners()
        setupInitialState()
    }

    private fun setupInitialState() {
        arguments
                ?.getJsonObject(ROUTINE_DATA, RoutineData::class.java)
                ?.let { sendIntent(AddRoutineIntent.PresetData(it)) }
                ?: render(AddRoutineViewState.Initial)
    }

    override fun onResume() {
        super.onResume()
        (context as? IActionbarActivity)?.setActionbarTitle(resources.getString(R.string.add_routine_title))
    }

    fun sendIntent(intent: AddRoutineIntent) =
        intentGuard.runIfFree { mviModel.postIntent(intent, viewModel.currentState) }


    private fun initIntentListeners() {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                sendIntent(AddRoutineIntent.OnUserLeaving)
            }
        })
        mView.daily_radio_button.setOnClickListener {
            sendIntent(AddRoutineIntent.RepeatTypeChanged(RepeatType.Daily))
        }
        mView.weekly_radio_button.setOnClickListener {
            sendIntent(AddRoutineIntent.RepeatTypeChanged(RepeatType.Weekly))
        }
        mView.save_button.setOnClickListener {
            sendIntent(AddRoutineIntent.SaveClicked)
        }
        mView.cancel_button.setOnClickListener {
            sendIntent(AddRoutineIntent.CancelledClicked)
        }
        mView.title_edit_text.addTextChangedListener {
            sendIntent(AddRoutineIntent.TitleChanged(it?.toString() ?: ""))
        }
        mView.mon_cb?.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Mon, isChecked) }
        mView.tue_cb?.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Tue, isChecked) }
        mView.wed_cb?.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Wed, isChecked) }
        mView.thu_cb?.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Thu, isChecked) }
        mView.fri_cb?.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Fri, isChecked) }
        mView.sat_cb?.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Sat, isChecked) }
        mView.sun_cb?.setOnCheckedChangeListener { _, isChecked -> onCheckChange(DayOfWeek.Sun, isChecked) }
    }

    override fun render(state: AddRoutineViewState) {
        when {
            state.finished -> finished()
            else -> {
                mView.title_edit_text.setTextIfDifferent(state.title)
                if (state.repeatType != viewModel.currentState?.repeatType) {
                    when (state.repeatType) {
                        RepeatType.Daily -> mView.daily_radio_button.isChecked = true
                        RepeatType.Weekly -> mView.weekly_radio_button.isChecked = true
                    }
                }
                DayOfWeek.values().forEach {
                    when (it) {
                        DayOfWeek.Mon -> mView.mon_cb.setCheckedIfDifferent(state.daysSelected.contains(it))
                        DayOfWeek.Tue -> mView.tue_cb.setCheckedIfDifferent(state.daysSelected.contains(it))
                        DayOfWeek.Wed -> mView.wed_cb.setCheckedIfDifferent(state.daysSelected.contains(it))
                        DayOfWeek.Thu -> mView.thu_cb.setCheckedIfDifferent(state.daysSelected.contains(it))
                        DayOfWeek.Fri -> mView.fri_cb.setCheckedIfDifferent(state.daysSelected.contains(it))
                        DayOfWeek.Sat -> mView.sat_cb.setCheckedIfDifferent(state.daysSelected.contains(it))
                        DayOfWeek.Sun -> mView.sun_cb.setCheckedIfDifferent(state.daysSelected.contains(it))
                    }
                }
                if (state.daysVisible != viewModel.currentState?.daysVisible) {
                    mView.day_selection_group.visibility = if (state.daysVisible) View.VISIBLE else View.GONE
                }
                if (state.saveEnabled != viewModel.currentState?.saveEnabled) {
                    mView.save_button.isEnabled = state.saveEnabled
                }
            }
        }
        viewModel.currentState = state
    }

    fun finished() {
        (context as? INavigationActivity)?.finishFragment()
    }

    private fun onCheckChange(dayOfWeek: DayOfWeek, checked: Boolean) {
        sendIntent(AddRoutineIntent.DayCheckedChange(dayOfWeek, checked))
    }

}