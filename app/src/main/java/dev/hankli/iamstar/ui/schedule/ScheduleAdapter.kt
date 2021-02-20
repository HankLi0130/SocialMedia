package dev.hankli.iamstar.ui.schedule

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Schedule
import dev.hankli.iamstar.utils.ext.toDateTimeString
import kotlinx.android.synthetic.main.card_schedule.view.*
import tw.hankli.brookray.core.constant.NO_RESOURCE
import tw.hankli.brookray.core.extension.viewOf

class ScheduleAdapter(options: FirestoreRecyclerOptions<Schedule>) :
    FirestoreRecyclerAdapter<Schedule, ScheduleAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.viewOf(R.layout.card_schedule)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Schedule) {
        holder.bind(model)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Schedule) {
            with(itemView) {
                val start = item.startDateTime.toDateTimeString()
                val end = item.endDateTime.toDateTimeString()
                val datetime = "$start ~ $end"
                view_schedule_datetime.text = datetime
                view_schedule_title.text = item.title
                view_schedule_location.text = item.location

                item.photoURL?.let { url ->
                    Glide.with(this).load(url).into(view_preview)
                } ?: view_preview.setImageResource(NO_RESOURCE)
            }
        }
    }
}