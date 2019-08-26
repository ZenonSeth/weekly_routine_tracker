package features.showroutines

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
import features.addroutine.AddRoutineFragment
import kotlinx.android.synthetic.main.show_routines_layout.view.*
import util.getApplicationComponent
import javax.inject.Inject

class ShowRoutinesFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    constructor() : this(0)

    @Inject
    lateinit var mviModel: ShowRoutinesModel

    private lateinit var mviView: ShowRoutinesView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getApplicationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.show_routines_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.all_routines_rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mviView = ViewModelProviders.of(this).get(ShowRoutinesView::class.java)
        mviView.init(this)
        mviModel.attachViewModel(mviView)
    }

    override fun onResume() {
        super.onResume()
        (context as? IActionbarActivity)?.setActionbarTitle(resources.getString(R.string.show_routines_title))
    }

    fun showNewRoutineFragment() {
        (context as? INavigationActivity)?.addFragment(AddRoutineFragment(), AddRoutineFragment::class.java.simpleName)
    }
}
