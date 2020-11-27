package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.constant.EMPTY
import tw.hankli.brookray.constant.ZERO

class Media(
    objectId: String = EMPTY,
    var url: String = EMPTY,
    var type: String = EMPTY,
    var width: Int = ZERO,
    var height: Int = ZERO,

    @get:PropertyName("thumbnail")
    @set:PropertyName("thumbnail")
    var thumbnailUrl: String = EMPTY
) : FirestoreModel(objectId)