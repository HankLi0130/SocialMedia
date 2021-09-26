package app.hankdev.data.models.firestore

import app.hankdev.data.enums.ReactionType
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
class Reaction(
    override var id: String = EMPTY,

    @get:PropertyName(REACTION_TYPE)
    @set:PropertyName(REACTION_TYPE)
    var reactionType: ReactionType? = null,

    @get:PropertyName(PROFILE)
    @set:PropertyName(PROFILE)
    var profile: DocumentReference? = null,

    override var createdAt: Date = Date(),
    override var updatedAt: Date? = null

) : FirestoreModel {
    companion object {
        const val REACTION_TYPE = "reactionType"
        const val PROFILE = "profile"
    }
}