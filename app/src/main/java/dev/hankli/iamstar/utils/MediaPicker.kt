package dev.hankli.iamstar.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import dev.hankli.iamstar.R
import dev.hankli.iamstar.data.models.Media
import kotlinx.android.synthetic.main.itemview_media.view.*
import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.constant.ZERO
import tw.hankli.brookray.extension.viewOf

val mediaPickerPermissions = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

private val imageTypes = setOf(MimeType.JPEG, MimeType.PNG)
private val videoTypes = setOf(MimeType.MPEG, MimeType.MP4)

fun showImagePicker(fragment: Fragment, maxSelectable: Int, requestCode: Int) {
    Matisse.from(fragment)
        .choose(imageTypes)
        .maxSelectable(maxSelectable)
        .countable(true)
        .capture(false)
//        .captureStrategy(CaptureStrategy(false, "${BuildConfig.APPLICATION_ID}.fileprovider"))
        .thumbnailScale(0.85f)
        .imageEngine(GlideEngine())
        .forResult(requestCode)
}

fun showVideoPicker(fragment: Fragment, maxSelectable: Int, requestCode: Int) {
    Matisse.from(fragment)
        .choose(videoTypes)
        .maxSelectable(maxSelectable)
        .countable(true)
        .capture(false)
//        .captureStrategy(CaptureStrategy(false, "${BuildConfig.APPLICATION_ID}.fileprovider"))
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

const val IMAGE = "image"
const val VIDEO = "video"
private const val MIME_TYPE = "mime_type"
private const val WIDTH = "width"
private const val HEIGHT = "height"

fun ContentResolver.toMediaItem(uri: Uri): MediaItem? {
    val cursor = this.query(
        uri,
        arrayOf(MIME_TYPE, WIDTH, HEIGHT),
        null,
        null,
        null,
        null
    )

    return cursor?.let {
        it.moveToFirst()
        val mimeType = it.getString(it.getColumnIndex(MIME_TYPE))
        val width = it.getInt(it.getColumnIndex(WIDTH))
        val height = it.getInt(it.getColumnIndex(HEIGHT))
        it.close()

        // type (image, video)
        val type = mimeType.split('/')[0]

        // Thumbnail
        val thumbnail = getThumbnail(uri, type)

        MediaItem(type = type, width = width, height = height, uri = uri, thumbnail = thumbnail)
    }
}

fun ContentResolver.getThumbnail(uri: Uri, type: String): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        loadThumbnail(uri, Size(96, 96), null)
    } else {
        when (type) {
            IMAGE -> {
                MediaStore.Images.Thumbnails.getThumbnail(
                    this,
                    uri.lastPathSegment!!.toLong(),
                    MediaStore.Images.Thumbnails.MICRO_KIND,
                    BitmapFactory.Options()
                )
            }
            VIDEO -> {
                MediaStore.Video.Thumbnails.getThumbnail(
                    this,
                    uri.lastPathSegment!!.toLong(),
                    MediaStore.Video.Thumbnails.MICRO_KIND,
                    BitmapFactory.Options()
                )
            }
            else -> null
        }
    }
}

// Model item, which can be local item or online item
data class MediaItem(
    val objectId: String = EMPTY,
    val url: String = EMPTY,
    val type: String = EMPTY,
    val width: Int = ZERO,
    val height: Int = ZERO,
    val thumbnailUrl: String = EMPTY,
    val uri: Uri? = null,
    val image: Bitmap? = null,
    val thumbnail: Bitmap? = null,
)

fun Media.toMediaItem(): MediaItem {
    return MediaItem(
        objectId = this.objectId,
        url = this.url,
        type = this.type,
        width = this.width,
        height = this.height,
        thumbnailUrl = this.thumbnailUrl
    )
}

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

                when {
                    item.thumbnail != null -> Glide.with(itemView).load(item.thumbnail)
                        .into(view_media_thumbnail)
                    item.url.isNotEmpty() -> Glide.with(itemView).load(item.url)
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