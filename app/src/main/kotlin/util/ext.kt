package util

import androidx.fragment.app.Fragment
import application.RoutineApplication

fun Fragment.getApplicationComponent() =
    (requireActivity().application as RoutineApplication).component()