package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.constant.ZERO
import java.util.*

@IgnoreExtraProperties
data class Post(
    var objectId: String = EMPTY,
    var influencerId: String = EMPTY,
    var authorId: String = EMPTY,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    var location: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var commentCount: Int = ZERO,
    var reactionCount: Int = ZERO,
    var content: String = EMPTY,
    @Exclude var medias: List<Media> = emptyList(),
    var reactions: Map<String, Int> = emptyMap()
)