package activity

import androidx.fragment.app.Fragment

interface INavigationActivity {
    fun addFragment(fragment: Fragment, tag: String?)
    fun finishFragment()
}