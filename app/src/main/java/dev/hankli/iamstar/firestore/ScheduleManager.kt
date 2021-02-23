package dev.hankli.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Schedule

class ScheduleManager(collection: CollectionReference) : FirestoreManager<Schedule>(collection) {

    fun queryByInfluencer(influencerDoc: DocumentReference, limit: Long = 10): Query {
        return rootCollection
            .whereEqualTo(Schedule.INFLUENCER, influencerDoc)
            .orderBy(Schedule.START_DATE_TIME, Query.Direction.ASCENDING)
            .limit(limit)
    }
}