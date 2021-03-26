package tw.iamstar.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import tw.iamstar.data.models.firestore.Schedule
import tw.iamstar.utils.getToday

class ScheduleManager(collection: CollectionReference) : FirestoreManager<Schedule>(collection) {

    fun queryByInfluencer(influencerDoc: DocumentReference, limit: Long = 10): Query {
        return rootCollection
            .whereEqualTo(Schedule.INFLUENCER, influencerDoc)
            .whereGreaterThanOrEqualTo(Schedule.START_DATE_TIME, getToday().time)
            .orderBy(Schedule.START_DATE_TIME, Query.Direction.ASCENDING)
            .limit(limit)
    }
}