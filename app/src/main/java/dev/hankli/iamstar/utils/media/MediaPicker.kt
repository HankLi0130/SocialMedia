package dev.hankli.iamstar.utils.media

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine

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
//        .thumbnailScale(0.85f)
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
//        .thumbnailScale(0.85f)
        .imageEngine(GlideEngine())
        .forResult(requestCode)
}

fun obtainResult(data: Intent?): List<Uri> {
    return data?.let { Matisse.obtainResult(it) } ?: emptyList()
}

fun obtainPathResult(data: Intent?): List<String> {
    return data?.let { Matisse.obtainPathResult(it) } ?: emptyList()
}