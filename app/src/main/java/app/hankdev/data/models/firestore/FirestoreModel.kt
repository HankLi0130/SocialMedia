package app.hankdev.data.models.firestore

import com.google.firebase.firestore.PropertyName

interface FirestoreModel {

    companion object {
        const val OBJECT_ID = "objectId"
    }

    @get:PropertyName(FirestoreModel.Companion.OBJECT_ID)
    @set:PropertyName(FirestoreModel.Companion.OBJECT_ID)
    var objectId: String
}