package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.DocumentReference
import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.constant.ZERO
import java.util.*

// https://stackoverflow.com/a/40117301/8361227
data class Feed(
    var objectId: String = EMPTY,
    var influencer: DocumentReference? = null,
    var author: DocumentReference? = null,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    var location: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var commentCount: Int = ZERO,
    var reactionCount: Int = ZERO,
    var content: String = EMPTY,
    var medias: List<Media> = emptyList()
)