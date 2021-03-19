package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import tw.iamstar.data.models.Installation

class InstallationManager(collection: CollectionReference) :
    FirestoreManager<Installation>(collection) {

    suspend fun removeFcmToken(id: String) {
        val updates = hashMapOf<String, Any>(Installation.FCM_TOKEN to FieldValue.delete())
        rootCollection.document(id).update(updates).await()
    }
}