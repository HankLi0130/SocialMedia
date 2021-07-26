package app.hankdev.firestore

import app.hankdev.data.models.firestore.Reaction
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await

class ReactionManager(collection: CollectionReference) : FirestoreManager<Reaction>(collection) {

    suspend fun exists(userId: String) = getDoc(userId).get().await().exists()

    suspend fun removeAll() {
        val docs = rootCollection.get().await().documents
        for (doc in docs) doc.reference.delete().await()
    }
}