package dev.hankli.iamstar.utils.media

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import kotlinx.android.synthetic.main.itemview_media.view.*
import tw.hankli.brookray.core.extension.viewOf

class MediaFileAdapter(private val listener: Listener) :
    RecyclerView.Adapter<MediaFileAdapter.ViewHolder>() {

    var items: List<MediaFile> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.viewOf(R.layout.itemview_media)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(media: MediaFile, listener: Listener) {
            with(itemView) {

                Glide.with(this).load(media.uri).into(view_media_thumbnail)

                view_cancel.setOnClickListener { listener.onItemCancel(bindingAdapterPosition) }
            }
        }
    }

    interface Listener {
        fun onItemCancel(position: Int)
    }
}