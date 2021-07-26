package app.hankdev.firestore

import app.hankdev.data.models.firestore.Schedule
import app.hankdev.utils.getToday
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query

class ScheduleManager(collection: CollectionReference) : FirestoreManager<Schedule>(collection) {

    fun queryByInfluencer(influencerDoc: DocumentReference, limit: Long = 10): Query {
        return rootCollection
            .whereEqualTo(Schedule.INFLUENCER, influencerDoc)
            .whereGreaterThanOrEqualTo(Schedule.START_DATE_TIME, getToday().time)
            .orderBy(Schedule.START_DATE_TIME, Query.Direction.ASCENDING)
            .limit(limit)
    }
}