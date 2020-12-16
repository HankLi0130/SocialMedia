package dev.hankli.iamstar.utils.media

import android.net.Uri

interface MediaFile {
    fun type(): String
}

data class LocalMediaFile(
    val uri: Uri,
    val path: String,
    val type: String
) : MediaFile {
    override fun type() = type
}

data class RemoteMediaFile(
    val id: String,
    val url: String,
    val type: String,
    val thumbnailUrl: String
) : MediaFile {
    override fun type() = type
}