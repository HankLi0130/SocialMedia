package tw.iamstar.data.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName
import tw.hankli.brookray.core.constant.EMPTY
import java.util.*

data class Installation(
    override var objectId: String = EMPTY,

    @get:PropertyName(FCM_TOKEN)
    @set:PropertyName(FCM_TOKEN)
    var fcmToken: String? = null,

    @get:PropertyName(APP_IDENTIFIER)
    @set:PropertyName(APP_IDENTIFIER)
    var appIdentifier: String? = null,

    @get:PropertyName(PROFILE)
    @set:PropertyName(PROFILE)
    var profile: DocumentReference? = null,

    @get:PropertyName(DEVICE_TYPE)
    @set:PropertyName(DEVICE_TYPE)
    var deviceType: String? = null,

    @get:PropertyName(CREATED_AT)
    @set:PropertyName(CREATED_AT)
    var createdAt: Date = Date(),

    @get:PropertyName(UPDATED_AT)
    @set:PropertyName(UPDATED_AT)
    var updatedAt: Date? = null
) : FirestoreModel {
    companion object {
        const val FCM_TOKEN = "fcmToken"
        const val APP_IDENTIFIER = "appIdentifier"
        const val PROFILE = "profile"
        const val DEVICE_TYPE = "deviceType"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
}
