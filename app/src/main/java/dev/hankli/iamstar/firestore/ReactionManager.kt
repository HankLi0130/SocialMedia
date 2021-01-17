package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import dev.hankli.iamstar.data.models.Reaction
import kotlinx.coroutines.tasks.await

class ReactionManager(collection: CollectionReference) : FirestoreManager<Reaction>(collection) {

    suspend fun exists(userId: String) = getDoc(userId).get().await().exists()

    suspend fun removeAll() {
        val docs = rootCollection.get().await().documents
        for (doc in docs) doc.reference.delete().await()
    }

    suspend fun count() = rootCollection.get().await().size()
}