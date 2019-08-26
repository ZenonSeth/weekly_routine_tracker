package di

import features.addroutine.AddRoutineFragment
import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidModule(private val context: Context) {
    @Provides
    fun context(): Context = context
}

@Singleton
@Component(modules = [AndroidModule::class])
interface RoutineAppComponent {
    fun inject(fragment: AddRoutineFragment)
}