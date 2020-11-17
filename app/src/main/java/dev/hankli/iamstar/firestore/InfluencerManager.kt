package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object InfluencerManager {
    private const val COLLECTION_INFLUENCER = "Influencer"

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val rootCollection: CollectionReference by lazy { db.collection(COLLECTION_INFLUENCER) }

    fun get(objectId: String): DocumentReference {
        return rootCollection.document(objectId)
    }
}