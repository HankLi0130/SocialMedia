package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import tw.iamstar.data.models.Installation

class InstallationManager(collection: CollectionReference) :
    FirestoreManager<Installation>(collection) {
}