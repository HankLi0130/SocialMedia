package tw.iamstar.utils.media

import android.net.Uri

interface MediaFile {
    val type: String
    val uri: Uri
}

data class LocalMediaFile(
    override val uri: Uri,
    val path: String,
    override val type: String
) : MediaFile

data class RemoteMediaFile(
    val id: String,
    val url: String,
    override val type: String,
    val thumbnailUrl: String
) : MediaFile {
    override val uri: Uri
        get() = Uri.parse(url)
}