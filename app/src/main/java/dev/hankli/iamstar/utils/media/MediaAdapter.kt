package dev.hankli.iamstar.utils.media

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.hankli.iamstar.R
import kotlinx.android.synthetic.main.itemview_media.view.*
import tw.hankli.brookray.extension.viewOf

class MediaAdapter(private val listener: Listener) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    var forBrowses: List<MediaForBrowse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.viewOf(R.layout.itemview_media)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(forBrowses[position], listener)
    }

    override fun getItemCount(): Int = forBrowses.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(forBrowse: MediaForBrowse, listener: Listener) {
            with(itemView) {

                when {
                    forBrowse.uri != null -> Glide.with(itemView).load(forBrowse.uri)
                        .into(view_media_thumbnail)
                    forBrowse.thumbnailUrl.isNotEmpty() -> Glide.with(itemView)
                        .load(forBrowse.thumbnailUrl)
                        .into(view_media_thumbnail)
                    else -> Glide.with(itemView).load(R.drawable.ic_broken_image)
                        .into(view_media_thumbnail)
                }

                view_cancel.setOnClickListener { listener.onItemCancel(adapterPosition) }
            }
        }
    }

    interface Listener {
        fun onItemCancel(position: Int)
    }
}