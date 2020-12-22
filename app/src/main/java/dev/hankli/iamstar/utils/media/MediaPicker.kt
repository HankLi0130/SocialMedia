package dev.hankli.iamstar.utils.media

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.IncapableCause
import com.zhihu.matisse.internal.entity.Item
import dev.hankli.iamstar.R

val mediaPickerPermissions = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
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
        .showSingleMediaType(true)
        .autoHideToolbarOnSingleTap(true)
        .imageEngine(GlideEngine())
        .forResult(requestCode)
}

fun showVideoPicker(fragment: Fragment, maxSelectable: Int, requestCode: Int) {
    Matisse.from(fragment)
        .choose(videoTypes)
        .maxSelectable(maxSelectable)
        .addFilter(VideoDurationFilter())
        .countable(true)
        .capture(false)
//        .captureStrategy(CaptureStrategy(false, "${BuildConfig.APPLICATION_ID}.fileprovider"))
        .thumbnailScale(0.85f)
        .showSingleMediaType(true)
        .autoHideToolbarOnSingleTap(true)
        .imageEngine(GlideEngine())
        .forResult(requestCode)
}

fun obtainResult(data: Intent?): List<Uri> {
    return data?.let { Matisse.obtainResult(it) } ?: emptyList()
}

fun obtainPathResult(data: Intent?): List<String> {
    return data?.let { Matisse.obtainPathResult(it) } ?: emptyList()
}

class VideoDurationFilter(private val duration: Long = 60000) : Filter() {
    override fun constraintTypes(): Set<MimeType> = videoTypes

    override fun filter(context: Context, item: Item): IncapableCause? {
        if (!needFiltering(context, item)) return null

        return if (item.duration > duration) {
            val seconds = duration / 1000
            IncapableCause(
                IncapableCause.TOAST,
                context.getString(R.string.error_video_duration, seconds)
            )
        } else null
    }

}