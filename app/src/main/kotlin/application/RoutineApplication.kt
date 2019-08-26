package application

import android.app.Application
import di.AndroidModule
import di.DaggerRoutineAppComponent
import di.RoutineAppComponent


class RoutineApplication : Application() {

    private lateinit var component: RoutineAppComponent
    fun component() = component
    override fun onCreate() {
        super.onCreate()
        component = DaggerRoutineAppComponent.builder()
                .androidModule(AndroidModule(this))
                .build()
    }
}