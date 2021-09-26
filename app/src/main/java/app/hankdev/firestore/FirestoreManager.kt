package app.hankdev.firestore

import app.hankdev.data.models.firestore.FirestoreModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await


open class FirestoreManager<M : FirestoreModel>(protected val rootCollection: CollectionReference) {

    fun getDoc(objectId: String): DocumentReference = rootCollection.document(objectId)

    fun observeDoc(
        objectId: String,
        listener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit
    ) = getDoc(objectId).addSnapshotListener(listener)

    suspend fun getDocSize() = rootCollection.get().await().size()

    suspend fun get(objectId: String, type: Class<M>): M? {
        return getDoc(objectId).get().await().toObject(type)
    }

    suspend fun add(model: M): String {
        val doc = rootCollection.document()
        model.id = doc.id
        doc.set(model).await()
        return doc.id
    }

    suspend fun set(model: M) = getDoc(model.id).set(model).await()

    suspend fun update(objectId: String, vararg updates: Pair<String, Any>) {
        rootCollection.document(objectId).update(hashMapOf(*updates)).await()
    }

    suspend fun remove(objectId: String) = getDoc(objectId).delete().await()

    fun getSubcollection(objectId: String, name: String) = getDoc(objectId).collection(name)
}