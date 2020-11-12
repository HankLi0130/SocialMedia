package dev.hankli.iamstar.utils.media

import android.content.ContentResolver
import android.net.Uri
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.utils.ext.*
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

data class MediaForBrowsing(
    val objectId: String = EMPTY,
    val url: String = EMPTY,
    val type: String = EMPTY,
    val width: Int = ZERO,
    val height: Int = ZERO,
    val thumbnailUrl: String = EMPTY,
    val uri: Uri? = null
)

fun Media.toForBrowsing(): MediaForBrowsing {
    return MediaForBrowsing(
        objectId = this.objectId,
        url = this.url,
        type = this.type,
        width = this.width,
        height = this.height,
        thumbnailUrl = this.thumbnailUrl
    )
}

data class MediaForUploading(
    val objectId: String,
    val file: ByteArray,
    val type: String,
    val width: Int,
    val height: Int,
    val thumbnail: ByteArray
)

fun getMediaItem(resolver: ContentResolver, uri: Uri): MediaForBrowsing? {
    val mimeType = resolver.getType(uri) ?: EMPTY
    return when {
        mimeType.contains(IMAGE) -> MediaForBrowsing(type = IMAGE, uri = uri)
        mimeType.contains(VIDEO) -> MediaForBrowsing(type = VIDEO, uri = uri)
        else -> null
    }
}

fun imageForUploading(
    resolver: ContentResolver,
    mediaForBrowsing: MediaForBrowsing
): Single<MediaForUploading> {
    return Single.create<MediaForUploading> { emitter ->
        val objectId = getObjectId()
        val bitmap = resolver.getBitmap(mediaForBrowsing.uri!!)

        val image = if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
            bitmap.scale(MAX_IMAGE_SIZE, true)
        } else bitmap

        val thumbnail =
            if (bitmap.width > MAX_THUMBNAIL_SIZE || bitmap.height > MAX_THUMBNAIL_SIZE) {
                bitmap.scale(MAX_THUMBNAIL_SIZE, true)
            } else bitmap

        emitter.onSuccess(
            MediaForUploading(
                objectId,
                image.toByteArray(),
                mediaForBrowsing.type,
                image.width,
                image.height,
                thumbnail.toByteArray()
            )
        )
    }.subscribeOn(Schedulers.computation())
}

fun videoForUploading(
    resolver: ContentResolver,
    mediaForBrowsing: MediaForBrowsing
): Single<MediaForUploading> {
    return Single.create<MediaForUploading> { emitter ->
        val objectId = getObjectId()
        val uri = mediaForBrowsing.uri!!
        val video = resolver.getByteArray(uri)
        val widthAndHeight = resolver.getWidthAndHeight(uri)
        val thumbnail = resolver.loadVideoThumbnail(uri).toByteArray()

        emitter.onSuccess(
            MediaForUploading(
                objectId,
                video,
                mediaForBrowsing.type,
                widthAndHeight.first,
                widthAndHeight.second,
                thumbnail
            )
        )
    }.subscribeOn(Schedulers.computation())
}

private fun getObjectId(): String = UUID.randomUUID().toString()