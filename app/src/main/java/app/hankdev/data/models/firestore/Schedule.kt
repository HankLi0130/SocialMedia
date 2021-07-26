package app.hankdev.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
data class Schedule(
    override var objectId: String = EMPTY,

    @get:PropertyName(Schedule.Companion.INFLUENCER)
    @set:PropertyName(Schedule.Companion.INFLUENCER)
    var influencer: DocumentReference? = null,

    @get:PropertyName(Schedule.Companion.AUTHOR)
    @set:PropertyName(Schedule.Companion.AUTHOR)
    var author: DocumentReference? = null,

    @get:PropertyName(Schedule.Companion.TITLE)
    @set:PropertyName(Schedule.Companion.TITLE)
    var title: String = EMPTY,

    @get:PropertyName(Schedule.Companion.LOCATION)
    @set:PropertyName(Schedule.Companion.LOCATION)
    var location: String? = null,

    @get:PropertyName(Schedule.Companion.LATITUDE)
    @set:PropertyName(Schedule.Companion.LATITUDE)
    var latitude: Double? = null,

    @get:PropertyName(Schedule.Companion.LONGITUDE)
    @set:PropertyName(Schedule.Companion.LONGITUDE)
    var longitude: Double? = null,

    @get:PropertyName(Schedule.Companion.START_DATE_TIME)
    @set:PropertyName(Schedule.Companion.START_DATE_TIME)
    var startDateTime: Date = Date(),

    @get:PropertyName(Schedule.Companion.END_DATE_TIME)
    @set:PropertyName(Schedule.Companion.END_DATE_TIME)
    var endDateTime: Date = Date(),

    @get:PropertyName(Schedule.Companion.MEDIA)
    @set:PropertyName(Schedule.Companion.MEDIA)
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
