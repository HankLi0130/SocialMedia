package tw.iamstar.data.models.firestore

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
data class Application(
    override var objectId: String = EMPTY,

    @get:PropertyName(NAME)
    @set:PropertyName(NAME)
    var name: String = EMPTY,

    @get:PropertyName(CREATED_AT)
    @set:PropertyName(CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(UPDATED_AT)
    @set:PropertyName(UPDATED_AT)
    var updatedAt: Date? = null

) : FirestoreModel {
    companion object {
        const val NAME = "name"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
}
