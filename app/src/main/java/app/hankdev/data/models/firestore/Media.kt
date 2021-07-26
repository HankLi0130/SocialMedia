package app.hankdev.data.models.firestore

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import tw.hankli.brookray.core.constant.ZERO

@IgnoreExtraProperties
class Media(
    override var objectId: String = EMPTY,

    @get:PropertyName(Media.Companion.URL)
    @set:PropertyName(Media.Companion.URL)
    var url: String = EMPTY,

    @get:PropertyName(Media.Companion.TYPE)
    @set:PropertyName(Media.Companion.TYPE)
    var type: String = EMPTY,

    @get:PropertyName(Media.Companion.WIDTH)
    @set:PropertyName(Media.Companion.WIDTH)
    var width: Int = ZERO,

    @get:PropertyName(Media.Companion.HEIGHT)
    @set:PropertyName(Media.Companion.HEIGHT)
    var height: Int = ZERO,

    @get:PropertyName(Media.Companion.THUMBNAIL_URL)
    @set:PropertyName(Media.Companion.THUMBNAIL_URL)
    var thumbnailUrl: String = EMPTY
) : FirestoreModel {
    companion object {
        const val URL = "url"
        const val TYPE = "type"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val THUMBNAIL_URL = "thumbnail"
    }
}