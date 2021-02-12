package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

data class Schedule(
    override var objectId: String = EMPTY,

    @get:PropertyName(TITLE)
    @set:PropertyName(TITLE)
    var title: String? = null,

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

    @get:PropertyName(PHOTO_URL)
    @set:PropertyName(PHOTO_URL)
    var photoURL: String? = null,

    ) : FirestoreModel {
    companion object {
        const val TITLE = "title"
        const val LOCATION = "location"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val START_DATE_TIME = "startDateTime"
        const val END_DATE_TIME = "endDateTime"
        const val PHOTO_URL = "photoURL"
    }
}
