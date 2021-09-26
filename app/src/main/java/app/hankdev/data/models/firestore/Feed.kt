package app.hankdev.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

// https://stackoverflow.com/a/40117301/8361227
@IgnoreExtraProperties
class Feed(
    override var id: String = EMPTY,

    @get:PropertyName(AUTHOR)
    @set:PropertyName(AUTHOR)
    var author: DocumentReference? = null,

    @get:PropertyName(LOCATION)
    @set:PropertyName(LOCATION)
    var location: String? = null,

    @get:PropertyName(LATITUDE)
    @set:PropertyName(LATITUDE)
    var latitude: Double? = null,

    @get:PropertyName(LONGITUDE)
    @set:PropertyName(LONGITUDE)
    var longitude: Double? = null,

    @get:PropertyName(CONTENT)
    @set:PropertyName(CONTENT)
    var content: String = EMPTY,

    @get:PropertyName(MEDIAS)
    @set:PropertyName(MEDIAS)
    var medias: List<Media> = emptyList(),

    @get:Exclude
    var photoURL: String? = null,

    @get:Exclude
    var reactionByCurrentUser: Reaction? = null,

    override var createdAt: Date = Date(),
    override var updatedAt: Date? = null

) : FirestoreModel {
    companion object {
        const val AUTHOR = "author"
        const val LOCATION = "location"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val COMMENT_COUNT = "commentCount"
        const val REACTION_COUNT = "reactionCount"
        const val CONTENT = "content"
        const val MEDIAS = "medias"
    }
}