package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.hankli.iamstar.data.models.Influencer

class InfluencerManager(collection: CollectionReference) : FirestoreManager<Influencer>(collection)