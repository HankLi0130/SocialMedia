package dev.hankli.iamstar.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
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
private const val WIDTH = "width"
private const val HEIGHT = "height"
private const val MAX_THUMBNAIL_PIXEL = 320
private const val MAX_IMAGE_PIXEL = 1440

fun getMediaItem(resolver: ContentResolver, uri: Uri): MediaItem? {
    val mimeType = resolver.getType(uri) ?: EMPTY
    return when {
        mimeType.contains(IMAGE) -> getImageItem(resolver, uri)
        mimeType.contains(VIDEO) -> getVideoItem(resolver, uri)
        else -> null
    }
}

fun getImageItem(resolver: ContentResolver, uri: Uri): MediaItem? {
    return null
}

private fun getVideoItem(resolver: ContentResolver, uri: Uri): MediaItem? {
    val cursor = resolver.query(
        uri,
        arrayOf(WIDTH, HEIGHT),
        null,
        null,
        null,
        null
    )

    return cursor?.let {
        it.moveToFirst()
        val width = it.getInt(it.getColumnIndex(WIDTH))
        val height = it.getInt(it.getColumnIndex(HEIGHT))
        it.close()

        // Thumbnail
        val thumbnail = resolver.getVideoThumbnail(uri)

        MediaItem(type = VIDEO, width = width, height = height, uri = uri, thumbnail = thumbnail)
    }
}

fun ContentResolver.getVideoThumbnail(uri: Uri): Bitmap {
    val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        loadThumbnail(uri, Size(MAX_THUMBNAIL_PIXEL, MAX_THUMBNAIL_PIXEL), null)
    } else {
        MediaStore.Video.Thumbnails.getThumbnail(
            this,
            uri.lastPathSegment!!.toLong(),
            MediaStore.Video.Thumbnails.MICRO_KIND,
            null
        )
    }
    // TODO The thumbnail isn't the same as the picture user selected, find the way to figure out.
    return thumbnail.scale(MAX_THUMBNAIL_PIXEL, true)
}

//fun ContentResolver.getImageThumbnail(uri: Uri): Bitmap {
//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        loadThumbnail(uri, Size(MAX_THUMBNAIL_PIXEL, MAX_THUMBNAIL_PIXEL), null)
//    } else {
//        MediaStore.Images.Thumbnails.getThumbnail(
//            this,
//            uri.lastPathSegment!!.toLong(),
//            MediaStore.Images.Thumbnails.MICRO_KIND,
//            BitmapFactory.Options()
//        )
//    }
//}

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