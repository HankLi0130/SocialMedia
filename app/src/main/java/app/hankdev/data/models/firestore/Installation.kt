package app.hankdev.data.models.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

@IgnoreExtraProperties
data class Installation(
    override var objectId: String = EMPTY,

    @get:PropertyName(Installation.Companion.FCM_TOKEN)
    @set:PropertyName(Installation.Companion.FCM_TOKEN)
    var fcmToken: String? = null,

    @get:PropertyName(Installation.Companion.PROFILE)
    @set:PropertyName(Installation.Companion.PROFILE)
    var profile: DocumentReference? = null,

    @get:PropertyName(Installation.Companion.DEVICE_TYPE)
    @set:PropertyName(Installation.Companion.DEVICE_TYPE)
    var deviceType: String? = null,

    @get:PropertyName(Installation.Companion.CREATED_AT)
    @set:PropertyName(Installation.Companion.CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(Installation.Companion.UPDATED_AT)
    @set:PropertyName(Installation.Companion.UPDATED_AT)
    var updatedAt: Date? = null
) : FirestoreModel {
    companion object {
        const val FCM_TOKEN = "fcmToken"
        const val PROFILE = "profile"
        const val DEVICE_TYPE = "deviceType"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
}
