package features.dailyroutine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.milchopenchev.weeklyexercisetracker.R
import features.activity.IActionbarActivity
import features.activity.INavigationActivity
import features.showroutines.ShowRoutinesFragment
import kotlinx.android.synthetic.main.daily_routine_layout.view.*
import kotlinx.android.synthetic.main.show_routines_layout.view.*
import util.getApplicationComponent
import javax.inject.Inject

class DailyRoutineFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    constructor() : this(0)

    @Inject
    lateinit var mviModel: DailyRoutineModel

    private lateinit var mviView: DailyRoutineView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getApplicationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.daily_routine_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.daily_routines_rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mviView = ViewModelProviders.of(this).get(DailyRoutineView::class.java)
        mviView.init(this)
        mviModel.attachViewModel(mviView)
    }

    override fun onResume() {
        super.onResume()
        (context as? IActionbarActivity)?.setActionbarTitle(resources.getString(R.string.daily_routine_title))
    }

    fun finished() {
        (context as? INavigationActivity)?.finishFragment()
    }

    fun showAllRoutinesFragment() {
        (context as? INavigationActivity)
                ?.addFragment(ShowRoutinesFragment(), ShowRoutinesFragment::class.java.simpleName)
    }
}