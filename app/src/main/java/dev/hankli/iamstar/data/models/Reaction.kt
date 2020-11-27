package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.DocumentReference
import tw.hankli.brookray.constant.EMPTY
import java.util.*

class Reaction(
    objectId: String = EMPTY,
    var reactionType: String = EMPTY,
    var profile: DocumentReference? = null,
    var createdAt: Date = Date()
) : FirestoreModel(objectId)