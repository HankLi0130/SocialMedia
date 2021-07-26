package app.hankdev.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
class Reaction(
    override var objectId: String = EMPTY,

    @get:PropertyName(Reaction.Companion.REACTION_TYPE)
    @set:PropertyName(Reaction.Companion.REACTION_TYPE)
    var reactionType: app.hankdev.data.enums.ReactionType? = null,

    @get:PropertyName(Reaction.Companion.PROFILE)
    @set:PropertyName(Reaction.Companion.PROFILE)
    var profile: DocumentReference? = null,

    @get:PropertyName(Reaction.Companion.CREATED_AT)
    @set:PropertyName(Reaction.Companion.CREATED_AT)
    var createdAt: Date = Date()
) : FirestoreModel {
    companion object {
        const val REACTION_TYPE = "reactionType"
        const val PROFILE = "profile"
        const val CREATED_AT = "createdAt"
    }
}