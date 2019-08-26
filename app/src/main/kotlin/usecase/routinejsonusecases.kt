package usecase

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import data.RoutinesListData
import javax.inject.Inject

class ConvertJsonToRoutinesList @Inject constructor() {
    operator fun invoke(jsonString: String): RoutinesListData =
            try {
                Gson().fromJson<RoutinesListData>(jsonString, RoutinesListData::class.java)
            } catch (ignored: Exception) {
                ignored.printStackTrace()
                RoutinesListData(emptySet())
            }
}

class ConvertRoutinesListToJson @Inject constructor(){
    operator fun invoke(routines: RoutinesListData): String =
            Gson().toJson(routines)
}