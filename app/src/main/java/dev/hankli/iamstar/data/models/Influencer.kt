package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.DocumentReference
import tw.hankli.brookray.constant.EMPTY

data class Influencer(
    var objectId: String = EMPTY,
    var profile: DocumentReference? = null,
)