package app.hankdev.data.models.firestore

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import tw.hankli.brookray.core.constant.ZERO
import java.util.*

@IgnoreExtraProperties
class Media(
    override var id: String = EMPTY,

    @get:PropertyName(URL)
    @set:PropertyName(URL)
    var url: String = EMPTY,

    @get:PropertyName(TYPE)
    @set:PropertyName(TYPE)
    var type: String = EMPTY,

    @get:PropertyName(WIDTH)
    @set:PropertyName(WIDTH)
    var width: Int = ZERO,

    @get:PropertyName(HEIGHT)
    @set:PropertyName(HEIGHT)
    var height: Int = ZERO,

    @get:PropertyName(THUMBNAIL_URL)
    @set:PropertyName(THUMBNAIL_URL)
    var thumbnailUrl: String = EMPTY,

    override var createdAt: Date = Date(),
    override var updatedAt: Date? = null

) : FirestoreModel {
    companion object {
        const val URL = "url"
        const val TYPE = "type"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val THUMBNAIL_URL = "thumbnail"
    }
}