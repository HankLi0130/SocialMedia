package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import dev.hankli.iamstar.data.models.Schedule

class ScheduleManager(collection: CollectionReference) : FirestoreManager<Schedule>(collection) {


}