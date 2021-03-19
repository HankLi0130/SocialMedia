package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import tw.iamstar.data.models.Installation
import java.util.*

class InstallationManager(collection: CollectionReference) :
    FirestoreManager<Installation>(collection) {

    suspend fun add(fcmToken: String, profile: DocumentReference, deviceType: String): String {
        val installation = Installation(
            fcmToken = fcmToken,
            profile = profile,
            deviceType = deviceType
        )
        return add(installation)
    }

    suspend fun update(
        id: String,
        fcmToken: String,
        profile: DocumentReference,
        deviceType: String
    ) {
        update(
            id,
            hashMapOf(
                Installation.FCM_TOKEN to fcmToken,
                Installation.PROFILE to profile,
                Installation.DEVICE_TYPE to deviceType,
                Installation.UPDATED_AT to Date()
            )
        )
    }

    suspend fun removeFcmToken(id: String) {
        update(id, hashMapOf(Installation.FCM_TOKEN to FieldValue.delete()))
    }
}