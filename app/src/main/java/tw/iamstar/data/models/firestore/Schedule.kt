package tw.iamstar.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
data class Schedule(
    override var objectId: String = EMPTY,

    @get:PropertyName(INFLUENCER)
    @set:PropertyName(INFLUENCER)
    var influencer: DocumentReference? = null,

    @get:PropertyName(AUTHOR)
    @set:PropertyName(AUTHOR)
    var author: DocumentReference? = null,

    @get:PropertyName(TITLE)
    @set:PropertyName(TITLE)
    var title: String = EMPTY,

    @get:PropertyName(LOCATION)
    @set:PropertyName(LOCATION)
    var location: String? = null,

    @get:PropertyName(LATITUDE)
    @set:PropertyName(LATITUDE)
    var latitude: Double? = null,

    @get:PropertyName(LONGITUDE)
    @set:PropertyName(LONGITUDE)
    var longitude: Double? = null,

    @get:PropertyName(START_DATE_TIME)
    @set:PropertyName(START_DATE_TIME)
    var startDateTime: Date = Date(),

    @get:PropertyName(END_DATE_TIME)
    @set:PropertyName(END_DATE_TIME)
    var endDateTime: Date = Date(),

    @get:PropertyName(MEDIA)
    @set:PropertyName(MEDIA)
    var media: Media? = null,

    @get:Exclude
    var photoURL: String? = null
) : FirestoreModel {
    companion object {
        const val INFLUENCER = "influencer"
        const val AUTHOR = "author"
        const val TITLE = "title"
        const val LOCATION = "location"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val START_DATE_TIME = "startDateTime"
        const val END_DATE_TIME = "endDateTime"
        const val MEDIA = "media"
    }
}
