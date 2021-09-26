package app.hankdev.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
data class Installation(
    override var id: String = EMPTY,

    @get:PropertyName(FCM_TOKEN)
    @set:PropertyName(FCM_TOKEN)
    var fcmToken: String? = null,

    @get:PropertyName(PROFILE)
    @set:PropertyName(PROFILE)
    var profile: DocumentReference? = null,

    @get:PropertyName(DEVICE_TYPE)
    @set:PropertyName(DEVICE_TYPE)
    var deviceType: String? = null,

    override var createdAt: Date = Date(),
    override var updatedAt: Date? = null

) : FirestoreModel {
    companion object {
        const val FCM_TOKEN = "fcmToken"
        const val PROFILE = "profile"
        const val DEVICE_TYPE = "deviceType"
    }
}
