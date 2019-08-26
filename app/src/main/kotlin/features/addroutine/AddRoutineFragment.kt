package features.addroutine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.milchopenchev.weeklyexercisetracker.R
import data.RoutineData
import features.activity.IActionbarActivity
import features.activity.INavigationActivity
import util.getApplicationComponent
import util.getJsonObject
import javax.inject.Inject


class AddRoutineFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    constructor() : this(0)

    companion object {
        const val ROUTINE_DATA = "routine_data"
    }

    @Inject
    lateinit var mviModel: AddRoutineModel

    private lateinit var mviView: AddRoutineView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getApplicationComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_routine_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mviView = ViewModelProviders.of(this).get(AddRoutineView::class.java)
        mviView.init(this)
        mviModel.attachViewModel(mviView)
        arguments
                ?.getJsonObject(ROUTINE_DATA, RoutineData::class.java)
                ?.let { mviModel.setRoutineData(it) }
    }

    override fun onResume() {
        super.onResume()
        (context as? IActionbarActivity)?.setActionbarTitle(resources.getString(R.string.add_routine_title))
    }

    fun finished() {
        (context as? INavigationActivity)?.finishFragment()
    }

}