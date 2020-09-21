package dev.hankli.iamstar.data.models

import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.constant.ZERO
import java.util.*

data class Post(
    var id: String = EMPTY,
    var authorID: String = EMPTY,
    var createdAt: Date? = null,
    var location: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var postComment: Int = ZERO,
    var postLikes: Int = ZERO,
    var postMedia: List<String> = emptyList(),
    var postText: String = EMPTY,
    var reactions: Map<String, Int> = emptyMap()
)