package features.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.milchopenchev.weeklyexercisetracker.R
import features.showroutines.ShowRoutinesFragment


class MainActivity : AppCompatActivity(), INavigationActivity, IActionbarActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.main_activity)
        addFragment(ShowRoutinesFragment(), ShowRoutinesFragment::class.java.simpleName)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun addFragment(fragment: Fragment, tag: String?) {
        if (supportFragmentManager.fragments.size > 0) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_holder, fragment, tag)
                    .addToBackStack(null)
                    .commit()
        } else {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_holder, fragment, tag)
                    .commit()
        }
    }

    override fun finishFragment() {
        supportFragmentManager.popBackStackImmediate()
    }

    override fun setActionbarTitle(title: String) {
        supportActionBar?.title = title
    }

}