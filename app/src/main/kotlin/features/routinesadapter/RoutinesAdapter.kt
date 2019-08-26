package features.routinesadapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import data.RoutineData

class RoutinesAdapter(private val context: Context, private val data: List<RoutineData>) :
        RecyclerView.Adapter<RoutinesViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutinesViewHolder =
            RoutinesViewHolder(context)

    override fun onBindViewHolder(holder: RoutinesViewHolder, position: Int) {
        holder.bind(data[position])
    }

}