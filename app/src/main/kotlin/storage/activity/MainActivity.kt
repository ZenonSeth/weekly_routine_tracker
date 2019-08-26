package storage.activity

import features.addroutine.AddRoutineFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.milchopenchev.weeklyexercisetracker.R

class MainActivity : AppCompatActivity(), INavigationActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.main_activity)
        addFragment(AddRoutineFragment(), "add_routine_fragment")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.fragments.size > 1) {
            supportFragmentManager.popBackStack()
        }
    }

    override fun addFragment(fragment: Fragment, tag: String?) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_holder, fragment, tag)
                .addToBackStack(null)
                .commit()
    }

    override fun finishFragment() {
        if (supportFragmentManager.fragments.size > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

}