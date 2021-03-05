package tw.iamstar.ui.schedule

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.card_schedule.view.*
import tw.hankli.brookray.core.constant.NO_RESOURCE
import tw.hankli.brookray.core.extension.viewOf
import tw.iamstar.R
import tw.iamstar.data.models.Schedule
import tw.iamstar.utils.ext.toDateTimeString

class ScheduleAdapter(options: FirestoreRecyclerOptions<Schedule>) :
    FirestoreRecyclerAdapter<Schedule, ScheduleAdapter.ViewHolder>(options) {

    var onItemLongClick: ((scheduleId: String) -> Boolean)? = null

    lateinit var onItemClick: (schedule: Schedule) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.viewOf(R.layout.card_schedule)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Schedule) {
        holder.bind(model, onItemLongClick, onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Schedule,
            onItemLongClick: ((scheduleId: String) -> Boolean)?,
            onItemClick: (schedule: Schedule) -> Unit
        ) {
            with(itemView) {
                item.photoURL?.let { url ->
                    Glide.with(this).load(url).into(view_profile_avatar.image)
                } ?: view_profile_avatar.image.setImageResource(R.drawable.ic_person)

                val start = item.startDateTime.toDateTimeString()
                val end = item.endDateTime.toDateTimeString()
                val datetime = "$start ~ $end"
                view_schedule_datetime.text = datetime
                view_schedule_title.text = item.title
                view_schedule_location.text = item.location

                item.media?.let { media ->
                    Glide.with(this).load(media.url).into(view_preview)
                } ?: view_preview.setImageResource(NO_RESOURCE)

                this.setOnLongClickListener { onItemLongClick?.invoke(item.objectId) ?: false }
                this.setOnClickListener { onItemClick(item) }
            }
        }
    }
}