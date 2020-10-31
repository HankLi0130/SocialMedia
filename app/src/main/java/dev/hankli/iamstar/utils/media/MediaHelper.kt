package dev.hankli.iamstar.utils.media

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.utils.ext.getBitmap
import dev.hankli.iamstar.utils.ext.scale
import dev.hankli.iamstar.utils.ext.toByteArray
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.constant.ZERO
import java.util.*

/**
 * Local Media Helper
 */

const val IMAGE = "image"
const val VIDEO = "video"
const val MAX_IMAGE_SIZE = 1440
const val MAX_THUMBNAIL_SIZE = 320

data class MediaForBrowse(
    val objectId: String = EMPTY,
    val url: String = EMPTY,
    val type: String = EMPTY,
    val width: Int = ZERO,
    val height: Int = ZERO,
    val thumbnailUrl: String = EMPTY,
    val uri: Uri? = null
)

fun Media.toForBrowse(): MediaForBrowse {
    return MediaForBrowse(
        objectId = this.objectId,
        url = this.url,
        type = this.type,
        width = this.width,
        height = this.height,
        thumbnailUrl = this.thumbnailUrl
    )
}

data class MediaForUpload(
    val objectId: String,
    val file: ByteArray,
    val type: String,
    val width: Int,
    val height: Int,
    val thumbnail: ByteArray
)

fun getMediaItem(resolver: ContentResolver, uri: Uri): MediaForBrowse? {
    val mimeType = resolver.getType(uri) ?: EMPTY
    return when {
        mimeType.contains(IMAGE) -> MediaForBrowse(type = IMAGE, uri = uri)
        mimeType.contains(VIDEO) -> MediaForBrowse(type = VIDEO, uri = uri)
        else -> null
    }
}

fun imageForUpload(
    resolver: ContentResolver,
    mediaForBrowse: MediaForBrowse
): Single<MediaForUpload> {
    Log.i("compress", "compress")
    return Single.create<MediaForUpload> { emitter ->
        val objectId = getObjectId()
        val bitmap = resolver.getBitmap(mediaForBrowse.uri!!)

        val image = if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
            bitmap.scale(MAX_IMAGE_SIZE, true)
        } else bitmap

        val thumbnail =
            if (bitmap.width > MAX_THUMBNAIL_SIZE || bitmap.height > MAX_THUMBNAIL_SIZE) {
                bitmap.scale(MAX_THUMBNAIL_SIZE, true)
            } else bitmap

        emitter.onSuccess(
            MediaForUpload(
                objectId,
                image.toByteArray(),
                mediaForBrowse.type,
                image.width,
                image.height,
                thumbnail.toByteArray()
            )
        )
    }.subscribeOn(Schedulers.computation())
}

//fun videoForUpload(resolver: ContentResolver, mediaForBrowse: MediaForBrowse): MediaForUpload {
//
//    return MediaForUpload()
//}

private fun getObjectId(): String = UUID.randomUUID().toString()

//fun ContentResolver.getVideoThumbnail(uri: Uri): Bitmap {
//    val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        loadThumbnail(uri, Size(MAX_THUMBNAIL_PIXEL, MAX_THUMBNAIL_PIXEL), null)
//    } else {
//        MediaStore.Video.Thumbnails.getThumbnail(
//            this,
//            uri.lastPathSegment!!.toLong(),
//            MediaStore.Video.Thumbnails.MICRO_KIND,
//            null
//        )
//    }
//    // TODO The thumbnail isn't the same as the picture user selected, find the way to figure out.
//    return thumbnail.scale(MAX_THUMBNAIL_PIXEL, true)
//}