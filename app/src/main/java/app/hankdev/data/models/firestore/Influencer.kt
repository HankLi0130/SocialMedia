package app.hankdev.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY

@IgnoreExtraProperties
class Influencer(
    override var objectId: String = EMPTY,

    @get:PropertyName(Influencer.Companion.PROFILE)
    @set:PropertyName(Influencer.Companion.PROFILE)
    var profile: DocumentReference? = null,
) : FirestoreModel {
    companion object {
        const val PROFILE = "profile"
    }
}