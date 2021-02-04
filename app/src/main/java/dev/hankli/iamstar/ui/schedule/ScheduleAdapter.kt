package dev.hankli.iamstar.ui.schedule

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Schedule
import kotlinx.android.synthetic.main.card_schedule.view.*
import tw.hankli.brookray.core.extension.viewOf

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    var item: List<Schedule> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.viewOf(R.layout.card_schedule))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item[position])
    }

    override fun getItemCount(): Int = item.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Schedule) {
            with(itemView) {
                view_schedule_title.text = item.title
                view_schedule_location.text = item.location
            }
        }
    }
}