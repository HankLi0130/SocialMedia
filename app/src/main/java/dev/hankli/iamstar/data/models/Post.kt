package dev.hankli.iamstar.data.models

import java.util.*

data class Post(
    var id: String,
    var authorID: String?,
    var createdAt: Date?,
    var location: String?,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var postComment: Int,
    var postLikes: Int,
    var postMedia: List<String>,
    var postText: String,
    var reactions: Map<String, Int>
)