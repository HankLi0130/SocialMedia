package dev.hankli.iamstar.data.models

import com.google.firebase.firestore.DocumentReference
import tw.hankli.brookray.core.constant.EMPTY

class Influencer(
    objectId: String = EMPTY,
    var profile: DocumentReference? = null,
) : FirestoreModel(objectId)