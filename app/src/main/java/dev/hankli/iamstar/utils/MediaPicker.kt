package dev.hankli.iamstar.utils

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import dev.hankli.iamstar.BuildConfig

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