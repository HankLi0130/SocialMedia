package dev.hankli.iamstar.utils

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine

private val mimeTypes = setOf(MimeType.JPEG, MimeType.PNG, MimeType.MPEG, MimeType.MP4)
private const val MAX_SELECTABLE = 5

fun showPicker(fragment: Fragment, requestCode: Int) {
    Matisse.from(fragment)
        .choose(mimeTypes)
        .countable(true)
        .maxSelectable(MAX_SELECTABLE)
        //.addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
        //.gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
        //.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        .thumbnailScale(0.85f)
        .imageEngine(GlideEngine())
        .showPreview(false) // Default is `true`
        .forResult(requestCode)
}

fun obtainResult(data: Intent?): List<Uri> {
    return data?.let { Matisse.obtainResult(it) } ?: emptyList()
}