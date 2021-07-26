package app.hankdev.firestore

import app.hankdev.data.models.firestore.Influencer
import com.google.firebase.firestore.CollectionReference

class InfluencerManager(collection: CollectionReference) : FirestoreManager<Influencer>(collection)