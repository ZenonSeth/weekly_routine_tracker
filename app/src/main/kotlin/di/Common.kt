package di

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import features.addroutine.AddRoutineFragment
import features.addroutine.AddRoutineModel
import features.dailyroutine.DailyRoutineFragment
import features.dailyroutine.DailyRoutineModel
import features.showroutines.ShowRoutinesFragment
import features.showroutines.ShowRoutinesModel
import javax.inject.Singleton

@Module
class AndroidModule(private val context: Context) {
    @Provides
    fun context(): Context = context
}

@Singleton
@Component(modules = [AndroidModule::class])
interface RoutineAppComponent {
    fun inject(model: AddRoutineModel)
    fun inject(model: ShowRoutinesModel)
    fun inject(model: DailyRoutineModel)
}