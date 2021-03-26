package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import tw.iamstar.data.models.firestore.Influencer

class InfluencerManager(collection: CollectionReference) : FirestoreManager<Influencer>(collection)