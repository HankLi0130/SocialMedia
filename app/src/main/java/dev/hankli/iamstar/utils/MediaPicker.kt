package dev.hankli.iamstar.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import dev.hankli.iamstar.BuildConfig
import dev.hankli.iamstar.R
import kotlinx.android.synthetic.main.itemview_media.view.*
import tw.hankli.brookray.extension.viewOf

private val mimeTypes = setOf(MimeType.JPEG, MimeType.PNG, MimeType.MPEG, MimeType.MP4)
private const val MAX_SELECTABLE = 5

fun showMediaPicker(fragment: Fragment, requestCode: Int) {
    Matisse.from(fragment)
        .choose(mimeTypes)
        .maxSelectable(MAX_SELECTABLE)
        .countable(true)
        .capture(true)
        .captureStrategy(CaptureStrategy(false, "${BuildConfig.APPLICATION_ID}.fileprovider"))
        .thumbnailScale(0.85f)
        .imageEngine(GlideEngine())
        .forResult(requestCode)
}

fun obtainResult(data: Intent?): List<Uri> {
    return data?.let { Matisse.obtainResult(it) } ?: emptyList()
}

fun obtainPathResult(data: Intent?): List<String> {
    return data?.let { Matisse.obtainPathResult(it) } ?: emptyList()
}

// Model
data class MediaItem(val uri: Uri, val type: String, val bitmap: Bitmap)

// RecyclerView Adapter
class MediaAdapter(private val listener: Listener) :
    RecyclerView.Adapter<MediaAdapter.MediaItemView>() {

    var items: List<MediaItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItemView {
        val view = parent.viewOf(R.layout.itemview_media)
        return MediaItemView(view)
    }

    override fun onBindViewHolder(holder: MediaItemView, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class MediaItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: MediaItem, listener: Listener) {
            with(itemView) {
                view_media_thumbnail.setImageBitmap(item.bitmap)
                view_cancel.setOnClickListener { listener.onItemCancel(adapterPosition) }
            }
        }
    }

    interface Listener {
        fun onItemCancel(position: Int)
    }
}