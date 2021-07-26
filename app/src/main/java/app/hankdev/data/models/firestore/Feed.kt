package app.hankdev.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import tw.hankli.brookray.core.constant.ZERO
import java.util.*

// https://stackoverflow.com/a/40117301/8361227
@IgnoreExtraProperties
class Feed(
    override var objectId: String = EMPTY,

    @get:PropertyName(Feed.Companion.INFLUENCER)
    @set:PropertyName(Feed.Companion.INFLUENCER)
    var influencer: DocumentReference? = null,

    @get:PropertyName(Feed.Companion.AUTHOR)
    @set:PropertyName(Feed.Companion.AUTHOR)
    var author: DocumentReference? = null,

    @get:PropertyName(Feed.Companion.CREATED_AT)
    @set:PropertyName(Feed.Companion.CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(Feed.Companion.UPDATED_AT)
    @set:PropertyName(Feed.Companion.UPDATED_AT)
    var updatedAt: Date? = null,

    @get:PropertyName(Feed.Companion.LOCATION)
    @set:PropertyName(Feed.Companion.LOCATION)
    var location: String? = null,

    @get:PropertyName(Feed.Companion.LATITUDE)
    @set:PropertyName(Feed.Companion.LATITUDE)
    var latitude: Double? = null,

    @get:PropertyName(Feed.Companion.LONGITUDE)
    @set:PropertyName(Feed.Companion.LONGITUDE)
    var longitude: Double? = null,

    @get:PropertyName(Feed.Companion.COMMENT_COUNT)
    @set:PropertyName(Feed.Companion.COMMENT_COUNT)
    var commentCount: Int = ZERO,

    @get:PropertyName(Feed.Companion.REACTION_COUNT)
    @set:PropertyName(Feed.Companion.REACTION_COUNT)
    var reactionCount: Int = ZERO,

    @get:PropertyName(Feed.Companion.CONTENT)
    @set:PropertyName(Feed.Companion.CONTENT)
    var content: String = EMPTY,

    @get:PropertyName(Feed.Companion.MEDIAS)
    @set:PropertyName(Feed.Companion.MEDIAS)
    var medias: List<Media> = emptyList(),

    @get:PropertyName(Feed.Companion.PIN_STATE)
    @set:PropertyName(Feed.Companion.PIN_STATE)
    var pinState: Boolean = false,

    @get:Exclude
    var photoURL: String? = null,

    @get:Exclude
    var reactionByCurrentUser: Reaction? = null
) : FirestoreModel {
    companion object {
        const val INFLUENCER = "influencer"
        const val AUTHOR = "author"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
        const val LOCATION = "location"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val COMMENT_COUNT = "commentCount"
        const val REACTION_COUNT = "reactionCount"
        const val CONTENT = "content"
        const val MEDIAS = "medias"
        const val PIN_STATE = "pinState"
    }
}