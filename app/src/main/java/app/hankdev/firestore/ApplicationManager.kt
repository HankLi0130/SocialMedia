package app.hankdev.firestore

import app.hankdev.BuildConfig
import app.hankdev.data.models.firestore.Application
import com.google.firebase.firestore.CollectionReference

class ApplicationManager(rootCollection: CollectionReference) :
    FirestoreManager<Application>(rootCollection) {

    suspend fun get(objectId: String) = get(objectId, Application::class.java)

    suspend fun get() = get(BuildConfig.FS_APPLICATION_ID)
}