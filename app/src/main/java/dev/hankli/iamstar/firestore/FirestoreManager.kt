package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import dev.hankli.iamstar.data.models.FirestoreModel
import kotlinx.coroutines.tasks.await

open class FirestoreManager<M : FirestoreModel>(protected val rootCollection: CollectionReference) {

    fun getDoc(objectId: String): DocumentReference = rootCollection.document(objectId)

    suspend fun getDocSize() = rootCollection.get().await().size()

    suspend fun get(objectId: String, type: Class<M>): M? {
        return getDoc(objectId).get().await().toObject(type)
    }

    suspend fun add(model: M) {
        val doc = rootCollection.document()
        model.objectId = doc.id
        doc.set(model).await()
    }

    suspend fun set(model: M) = getDoc(model.objectId).set(model).await()

    suspend fun remove(objectId: String) = getDoc(objectId).delete().await()

    fun getSubcollection(objectId: String, name: String) = getDoc(objectId).collection(name)
}