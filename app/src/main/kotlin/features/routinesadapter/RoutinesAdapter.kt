package features.routinesadapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import data.RoutineData


enum class RoutinesAdapterMode {
    AllDisplay, DailyDisplay
}

class RoutinesAdapter(
        private val context: Context,
        private val data: List<RoutineData>,
        private val mode: RoutinesAdapterMode) :
        RecyclerView.Adapter<RoutinesViewHolder>() {

    override fun getItemCount(): Int = data.size
    private var onClickListener: (RoutineData) -> Unit = {}
    private var onLongClickListener: (RoutineData) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutinesViewHolder =
            RoutinesViewHolder(context, parent, mode).also { viewHolder ->
                viewHolder.setListeners({ onClickListener(it) }, { onLongClickListener(it) })
            }

    override fun onBindViewHolder(holder: RoutinesViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun setOnItemClickListener(listener: (RoutineData) -> Unit) {
        onClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (RoutineData) -> Unit) {
        onLongClickListener = listener
    }

}