package app.hankdev.data.models.firestore

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
data class Application(
    override var objectId: String = EMPTY,

    @get:PropertyName(Application.Companion.NAME)
    @set:PropertyName(Application.Companion.NAME)
    var name: String = EMPTY,

    @get:PropertyName(Application.Companion.CREATED_AT)
    @set:PropertyName(Application.Companion.CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(Application.Companion.UPDATED_AT)
    @set:PropertyName(Application.Companion.UPDATED_AT)
    var updatedAt: Date? = null

) : FirestoreModel {
    companion object {
        const val NAME = "name"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
}
