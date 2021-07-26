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

    @get:PropertyName(Comment.Companion.PROFILE)
    @set:PropertyName(Comment.Companion.PROFILE)
    var profile: DocumentReference? = null,

    @get:PropertyName(Comment.Companion.CONTENT)
    @set:PropertyName(Comment.Companion.CONTENT)
    var content: String = EMPTY,

    @get:PropertyName(Comment.Companion.CREATED_AT)
    @set:PropertyName(Comment.Companion.CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(Comment.Companion.UPDATED_AT)
    @set:PropertyName(Comment.Companion.UPDATED_AT)
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