package features.routinesadapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.milchopenchev.weeklyexercisetracker.R
import data.RoutineData
import enums.RepeatType
import kotlinx.android.synthetic.main.routine_list_item.view.*

class RoutinesViewHolder(context: Context)
    : RecyclerView.ViewHolder(View.inflate(context, R.layout.routine_list_item, null)) {

    fun bind(data: RoutineData) {
        when (data.type) {
            RepeatType.Daily -> bindDailyData(data)
            RepeatType.Weekly -> bindWeeklyData(data)
        }
    }

    private fun bindDailyData(data: RoutineData) {
        itemView.routine_item_title.text = data.description
        itemView.routine_item_repeat_type.setText(R.string.daily)
    }

    private fun bindWeeklyData(data: RoutineData) {
        itemView.routine_item_title.text = data.description
        itemView.routine_item_repeat_type.setText(R.string.weekly)
    }


}