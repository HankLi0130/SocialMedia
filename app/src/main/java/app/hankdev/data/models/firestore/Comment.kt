package app.hankdev.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY

import java.util.*

@IgnoreExtraProperties
class Comment(
    override var objectId: String = EMPTY,

    @get:PropertyName(PROFILE)
    @set:PropertyName(PROFILE)
    var profile: DocumentReference? = null,

    @get:PropertyName(CONTENT)
    @set:PropertyName(CONTENT)
    var content: String = EMPTY,

    @get:PropertyName(CREATED_AT)
    @set:PropertyName(CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(UPDATED_AT)
    @set:PropertyName(UPDATED_AT)
    var updatedAt: Date? = null,

    @get:Exclude
    var photoURL: String? = null,

    @get:Exclude
    var userName: String? = null
) : FirestoreModel {
    companion object {
        const val PROFILE = "profile"
        const val CONTENT = "content"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
}