package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import tw.iamstar.data.models.Reaction

class ReactionManager(collection: CollectionReference) : FirestoreManager<Reaction>(collection) {

    suspend fun exists(userId: String) = getDoc(userId).get().await().exists()

    suspend fun removeAll() {
        val docs = rootCollection.get().await().documents
        for (doc in docs) doc.reference.delete().await()
    }
}