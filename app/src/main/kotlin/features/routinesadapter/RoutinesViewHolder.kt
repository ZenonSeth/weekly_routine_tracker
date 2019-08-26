package features.routinesadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.milchopenchev.weeklyexercisetracker.R
import data.RoutineData
import enums.DayOfWeek
import enums.RepeatType
import kotlinx.android.synthetic.main.routine_list_item.view.*
import usecase.DayOfWeekFromTime

private fun CheckBox.setCheckedIfDifferent(checked: Boolean) {
    if (isChecked != checked) {
        isChecked = checked
    }
}


class RoutinesViewHolder(val context: Context, parent: ViewGroup, val mode: RoutinesAdapterMode)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.routine_list_item, parent, false)) {

    val daysOfWeekFromTime = DayOfWeekFromTime()

    init {
        itemView.setOnClickListener { onClickListener(data!!) }
        itemView.setOnLongClickListener { onLongClickListener(data!!); true }
        itemView.routine_item_checkbox.setOnClickListener { onClickListener(data!!) }
    }

    private var onClickListener: (RoutineData) -> Unit = {}
    private var onLongClickListener: (RoutineData) -> Unit = {}

    private var data: RoutineData? = null

    fun bind(data: RoutineData) {
        this.data = data
        itemView.routine_item_title.text = data.description

        when (mode) {
            RoutinesAdapterMode.DailyDisplay -> {
                itemView.routine_item_checkbox.visibility = View.VISIBLE
                itemView.routine_item_checkbox.setCheckedIfDifferent(isCompleted(data))
                itemView.routine_item_repeat_type.visibility = View.GONE
            }
            RoutinesAdapterMode.AllDisplay -> {
                itemView.routine_item_repeat_type.text = getRepeatTypeText(data)
                itemView.routine_item_repeat_type.visibility = View.VISIBLE
                itemView.routine_item_checkbox.visibility = View.GONE
            }
        }
    }

    private fun getRepeatTypeText(data: RoutineData): String =
            if (data.type == RepeatType.Daily) {
                context.resources.getString(R.string.daily)
            } else {
                getFullText(context.resources.getString(R.string.weekly), data.days.sorted())
            }

    private fun isCompleted(data: RoutineData): Boolean =
            if (data.type == RepeatType.Daily) {
                data.completed
            } else {
                data.completedDays.contains(daysOfWeekFromTime(System.currentTimeMillis()))
            }

    private fun getFullText(weeklyPrefix: String, days: List<DayOfWeek>): String =
            weeklyPrefix + days.fold(": ") { acc, dayOfWeek -> "$acc $dayOfWeek" }

    fun setListeners(onClickListener: (RoutineData) -> Unit, onLongClickListener: (RoutineData) -> Unit) {
        this.onClickListener = onClickListener
        this.onLongClickListener = onLongClickListener
    }


}