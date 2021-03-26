package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import tw.iamstar.BuildConfig
import tw.iamstar.data.models.firestore.Application

class ApplicationManager(rootCollection: CollectionReference) :
    FirestoreManager<Application>(rootCollection) {

    suspend fun get(objectId: String) = get(objectId, Application::class.java)

    suspend fun get() = get(BuildConfig.FS_APPLICATION_ID)
}