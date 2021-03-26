package tw.iamstar.data.models.firestore

import com.google.firebase.firestore.PropertyName

interface FirestoreModel {

    companion object {
        const val OBJECT_ID = "objectId"
    }

    @get:PropertyName(OBJECT_ID)
    @set:PropertyName(OBJECT_ID)
    var objectId: String
}