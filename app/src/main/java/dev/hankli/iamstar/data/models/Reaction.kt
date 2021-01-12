package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName
import dev.hankli.iamstar.data.enums.ReactionType
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

class Reaction(
    override var objectId: String = EMPTY,

    @get:PropertyName(REACTION_TYPE)
    @set:PropertyName(REACTION_TYPE)
    var reactionType: ReactionType? = null,

    @get:PropertyName(PROFILE)
    @set:PropertyName(PROFILE)
    var profile: DocumentReference? = null,

    @get:PropertyName(CREATED_AT)
    @set:PropertyName(CREATED_AT)
    var createdAt: Date = Date()
) : FirestoreModel {
    companion object {
        const val REACTION_TYPE = "reactionType"
        const val PROFILE = "profile"
        const val CREATED_AT = "createdAt"
    }
}