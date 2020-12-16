package dev.hankli.iamstar.utils.media

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.utils.ext.getWidthAndHeight
import tw.hankli.brookray.core.constant.EMPTY
import tw.hankli.brookray.core.extension.getImageThumbnail
import tw.hankli.brookray.core.extension.getVideoThumbnail
import java.util.*

/**
 * Local Media Helper
 */

const val IMAGE = "image"
const val VIDEO = "video"
const val MAX_IMAGE_SIZE = 1440
const val MAX_THUMBNAIL_SIZE = 320

fun Media.toMediaFile() = RemoteMediaFile(
    this.objectId,
    this.url,
    this.type,
    this.thumbnailUrl
)

data class UploadingMedia(
    val objectId: String,
    val file: Uri,
    val type: String,
    val width: Int,
    val height: Int,
    val thumbnail: Bitmap
)

@Throws(IllegalArgumentException::class)
fun toMediaFiles(resolver: ContentResolver, uris: List<Uri>, paths: List<String>): List<MediaFile> {
    if (uris.size != paths.size) throw IllegalArgumentException("The sizes of Uri and Path have to be the same !")

    val mediaFiles = mutableListOf<MediaFile>()

    for (num in uris.indices) {
        val uri = uris[num]
        val path = paths[num]
        val mimeType = resolver.getType(uri) ?: EMPTY
        val type = when {
            mimeType.contains(IMAGE) -> IMAGE
            mimeType.contains(VIDEO) -> VIDEO
            else -> throw IllegalArgumentException("Unknown media type !")
        }
        mediaFiles.add(LocalMediaFile(uri, path, type))
    }
    return mediaFiles
}

fun toUploadingMedias(
    contentResolver: ContentResolver,
    localMediaFiles: List<LocalMediaFile>
): List<UploadingMedia> {
    return localMediaFiles.map { mediaFile ->
        val objectId = getRandomId()
        val widthAndHeight = contentResolver.getWidthAndHeight(mediaFile.uri)
        val thumbnail = when (mediaFile.type) {
            IMAGE -> contentResolver.getImageThumbnail(mediaFile.uri)
            VIDEO -> contentResolver.getVideoThumbnail(mediaFile.uri)
            else -> throw IllegalArgumentException("Unknown type !")
        }

        UploadingMedia(
            objectId,
            mediaFile.uri,
            mediaFile.type,
            widthAndHeight.first,
            widthAndHeight.second,
            thumbnail
        )
    }
}

//fun imageForUploading(
//    resolver: ContentResolver,
//    mediaForBrowsing: MediaForBrowsing
//): Single<MediaForUploading> {
//    return Single.create<MediaForUploading> { emitter ->
//        val objectId = getObjectId()
//        val bitmap = resolver.getBitmap(mediaForBrowsing.uri!!)
//
//        val image = if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
//            bitmap.scale(MAX_IMAGE_SIZE, true)
//        } else bitmap
//
//        val thumbnail =
//            if (bitmap.width > MAX_THUMBNAIL_SIZE || bitmap.height > MAX_THUMBNAIL_SIZE) {
//                bitmap.scale(MAX_THUMBNAIL_SIZE, true)
//            } else bitmap
//
//        emitter.onSuccess(
//            MediaForUploading(
//                objectId,
//                image.toByteArray(),
//                mediaForBrowsing.type,
//                image.width,
//                image.height,
//                thumbnail.toByteArray()
//            )
//        )
//    }.subscribeOn(Schedulers.computation())
//}

//fun videoForUploading(
//    resolver: ContentResolver,
//    mediaForBrowsing: MediaForBrowsing
//): Single<MediaForUploading> {
//    return Single.create<MediaForUploading> { emitter ->
//        val objectId = getObjectId()
//        val uri = mediaForBrowsing.uri!!
//        val video = resolver.getByteArray(uri)
//        val widthAndHeight = resolver.getWidthAndHeight(uri)
//        val thumbnail = resolver.loadVideoThumbnail(uri).toByteArray()
//
//        emitter.onSuccess(
//            MediaForUploading(
//                objectId,
//                video,
//                mediaForBrowsing.type,
//                widthAndHeight.first,
//                widthAndHeight.second,
//                thumbnail
//            )
//        )
//    }.subscribeOn(Schedulers.computation())
//}

fun getRandomId(): String = UUID.randomUUID().toString()