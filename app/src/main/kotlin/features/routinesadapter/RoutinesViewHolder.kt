package features.routinesadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.milchopenchev.weeklyexercisetracker.R
import data.RoutineData
import enums.DayOfWeek
import enums.RepeatType
import kotlinx.android.synthetic.main.routine_list_item.view.*

class RoutinesViewHolder(val context: Context, parent: ViewGroup)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.routine_list_item, parent, false)) {

    init {
        itemView.setOnClickListener { onClickListener(data!!) }
        itemView.setOnLongClickListener { onLongClickListener(data!!); true }
    }

    private var onClickListener: (RoutineData) -> Unit = {}
    private var onLongClickListener: (RoutineData) -> Unit = {}

    private var data: RoutineData? = null

    fun bind(data: RoutineData) {
        this.data = data
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
        val weeklyPrefix = context.resources.getString(R.string.weekly)
        itemView.routine_item_repeat_type.text = getFullText(weeklyPrefix, data.days.sorted())
    }

    private fun getFullText(weeklyPrefix: String, days: List<DayOfWeek>): String =
            weeklyPrefix + days.fold(": ") { acc, dayOfWeek -> "$acc $dayOfWeek" }

    fun setListeners(onClickListener: (RoutineData) -> Unit, onLongClickListener: (RoutineData) -> Unit) {
        this.onClickListener = onClickListener
        this.onLongClickListener = onLongClickListener
    }


}