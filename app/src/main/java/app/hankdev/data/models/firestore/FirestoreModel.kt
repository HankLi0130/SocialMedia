package app.hankdev.data.models.firestore

import com.google.firebase.firestore.PropertyName
import java.util.*

interface FirestoreModel {

    companion object {
        const val ID = "id"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }

    @get:PropertyName(ID)
    @set:PropertyName(ID)
    var id: String

    @get:PropertyName(CREATED_AT)
    @set:PropertyName(CREATED_AT)
    var createdAt: Date

    @get:PropertyName(UPDATED_AT)
    @set:PropertyName(UPDATED_AT)
    var updatedAt: Date?
}