package features.addroutine

import storage.activity.INavigationActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.milchopenchev.weeklyexercisetracker.R
import util.getApplicationComponent
import javax.inject.Inject


class AddRoutineFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    constructor() : this(0)

    @Inject
    lateinit var mviModel: AddRoutineModel

    private lateinit var mviView: AddRoutineView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_routine_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getApplicationComponent().inject(this)
        mviView = ViewModelProviders.of(this).get(AddRoutineView::class.java)
        mviView.init(this)
        mviModel.attachViewModel(mviView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    fun finished() {
        (context as? INavigationActivity)?.apply { finishFragment() }
    }

}