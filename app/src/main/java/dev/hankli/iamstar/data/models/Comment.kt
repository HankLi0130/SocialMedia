package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import tw.hankli.brookray.core.constant.EMPTY

import java.util.*

@IgnoreExtraProperties
class Comment(
    objectId: String = EMPTY,
    var profile: DocumentReference? = null,
    var content: String = EMPTY,
    var createdAt: Date = Date(),
    var updatedAt: Date? = null,

    @get:Exclude
    var commenterHeadshot: String = EMPTY,

    @get:Exclude
    var commenterName: String = EMPTY
) : FirestoreModel(objectId)