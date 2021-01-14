package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.hankli.iamstar.data.models.FirestoreModel
import kotlinx.coroutines.tasks.await

open class FirestoreManager<M : FirestoreModel>(db: FirebaseFirestore, collectionName: String) {

    val collection = db.collection(collectionName)

    suspend fun add(model: M) {
        val doc = collection.document()
        model.objectId = doc.id
        doc.set(model).await()
    }

    suspend fun set(model: M) {
        collection.document(model.objectId)
            .set(model)
            .await()
    }

    suspend fun remove(objectId: String) {
        collection.document(objectId)
            .delete()
            .await()
    }

    fun getDoc(objectId: String): DocumentReference = collection.document(objectId)

}