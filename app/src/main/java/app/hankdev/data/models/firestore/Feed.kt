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

    @get:PropertyName(INFLUENCER)
    @set:PropertyName(INFLUENCER)
    var influencer: DocumentReference? = null,

    @get:PropertyName(AUTHOR)
    @set:PropertyName(AUTHOR)
    var author: DocumentReference? = null,

    @get:PropertyName(CREATED_AT)
    @set:PropertyName(CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(UPDATED_AT)
    @set:PropertyName(UPDATED_AT)
    var updatedAt: Date? = null,

    @get:PropertyName(LOCATION)
    @set:PropertyName(LOCATION)
    var location: String? = null,

    @get:PropertyName(LATITUDE)
    @set:PropertyName(LATITUDE)
    var latitude: Double? = null,

    @get:PropertyName(LONGITUDE)
    @set:PropertyName(LONGITUDE)
    var longitude: Double? = null,

    @get:PropertyName(COMMENT_COUNT)
    @set:PropertyName(COMMENT_COUNT)
    var commentCount: Int = ZERO,

    @get:PropertyName(REACTION_COUNT)
    @set:PropertyName(REACTION_COUNT)
    var reactionCount: Int = ZERO,

    @get:PropertyName(CONTENT)
    @set:PropertyName(CONTENT)
    var content: String = EMPTY,

    @get:PropertyName(MEDIAS)
    @set:PropertyName(MEDIAS)
    var medias: List<Media> = emptyList(),

    @get:PropertyName(PIN_STATE)
    @set:PropertyName(PIN_STATE)
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