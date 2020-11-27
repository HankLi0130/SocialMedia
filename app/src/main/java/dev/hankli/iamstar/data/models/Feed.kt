package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.constant.ZERO
import java.util.*

// https://stackoverflow.com/a/40117301/8361227
@IgnoreExtraProperties
class Feed(
    objectId: String = EMPTY,
    var influencer: DocumentReference? = null,
    var author: DocumentReference? = null,
    var createdAt: Date = Date(),
    var updatedAt: Date? = null,
    var location: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var commentCount: Int = ZERO,
    var reactionCount: Int = ZERO,
    var content: String = EMPTY,
    var medias: List<Media> = emptyList(),

    @get:Exclude
    var reaction: Reaction? = null
) : FirestoreModel(objectId)