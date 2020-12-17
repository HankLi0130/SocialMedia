package dev.hankli.iamstar.utils.media

import android.content.ContentResolver
import android.net.Uri
import dev.hankli.iamstar.data.models.Media
import tw.hankli.brookray.core.constant.EMPTY
import tw.hankli.brookray.core.extension.*
import java.io.InputStream
import java.util.*

/**
 * Local Media Helper
 */

const val IMAGE = "image"
const val VIDEO = "video"
const val MAX_IMAGE_SIZE = 1440
const val MAX_THUMBNAIL_SIZE = 320

fun getRandomId(): String = UUID.randomUUID().toString()

fun Media.toMediaFile() = RemoteMediaFile(
    this.objectId,
    this.url,
    this.type,
    this.thumbnailUrl
)

data class UploadingMedia(
    val objectId: String,
    val file: InputStream,
    val type: String,
    val width: Int,
    val height: Int,
    val thumbnail: InputStream
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
        return@map when (mediaFile.type) {
            IMAGE -> produceUploadingImage(contentResolver, mediaFile)
            VIDEO -> produceUploadingVideo(contentResolver, mediaFile)
            else -> throw IllegalArgumentException("Unknown type !")
        }
    }
}

private fun produceUploadingImage(
    contentResolver: ContentResolver,
    mediaFile: LocalMediaFile,
): UploadingMedia {
    val objectId = getRandomId()
    val bitmap = contentResolver.getBitmap(mediaFile.uri)

    val image = if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
        bitmap.scale(MAX_IMAGE_SIZE, true)
    } else bitmap

    val thumbnail = if (bitmap.width > MAX_THUMBNAIL_SIZE || bitmap.height > MAX_THUMBNAIL_SIZE) {
        bitmap.scale(MAX_THUMBNAIL_SIZE, true)
    } else bitmap

    return UploadingMedia(
        objectId,
        image.toByteArrayInputStream(),
        mediaFile.type,
        image.width,
        image.height,
        thumbnail.toByteArrayInputStream()
    )
}

private fun produceUploadingVideo(
    contentResolver: ContentResolver,
    mediaFile: LocalMediaFile
): UploadingMedia {
    val objectId = getRandomId()
    // TODO compress video
    val video = contentResolver.openInputStream(mediaFile.uri)!!
    val widthAndHeight = contentResolver.getWidthAndHeight(mediaFile.uri)
    val bitmap = contentResolver.getVideoThumbnail(mediaFile.uri, ThumbnailSize.MINI_KIND)

    val thumbnail = if (bitmap.width > MAX_THUMBNAIL_SIZE || bitmap.height > MAX_THUMBNAIL_SIZE) {
        bitmap.scale(MAX_THUMBNAIL_SIZE, true)
    } else bitmap

    return UploadingMedia(
        objectId,
        video,
        mediaFile.type,
        widthAndHeight.first,
        widthAndHeight.second,
        thumbnail.toByteArrayInputStream()
    )
}