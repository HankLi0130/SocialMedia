package dev.hankli.iamstar.repo

import com.google.firebase.firestore.Query
import dev.hankli.iamstar.data.models.Media
import dev.hankli.iamstar.data.models.Schedule
import dev.hankli.iamstar.firebase.BUCKET_SCHEDULE
import dev.hankli.iamstar.firebase.StorageManager
import dev.hankli.iamstar.firestore.InfluencerManager
import dev.hankli.iamstar.firestore.ProfileManager
import dev.hankli.iamstar.firestore.ScheduleManager
import dev.hankli.iamstar.utils.media.UploadingMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import tw.hankli.brookray.core.constant.EMPTY

class ScheduleRepo(
    private val scheduleManager: ScheduleManager,
    private val influencerManager: InfluencerManager,
    private val profileManager: ProfileManager
) {
    suspend fun add(
        coroutineScope: CoroutineScope,
        schedule: Schedule,
        currentUserId: String,
        influencerId: String,
        uploadingMedia: UploadingMedia?
    ) {
        schedule.author = profileManager.getDoc(currentUserId)
        schedule.influencer = influencerManager.getDoc(influencerId)
        uploadingMedia?.run {
            schedule.media = uploadScheduleMedia(coroutineScope, this)
        }
        scheduleManager.add(schedule)
    }

    suspend fun get(scheduleId: String) = scheduleManager.get(scheduleId, Schedule::class.java)

    private suspend fun uploadScheduleMedia(scope: CoroutineScope, media: UploadingMedia): Media {
        val filePath = "$BUCKET_SCHEDULE/${media.objectId}"
        val fileUrl = scope.async { StorageManager.uploadFile(filePath, media.file) }

        return Media(
            media.objectId,
            fileUrl.await(),
            media.type,
            media.width,
            media.height,
            EMPTY
        )
    }

    fun queryByInfluencer(influencerId: String): Query {
        return scheduleManager.queryByInfluencer(influencerManager.getDoc(influencerId))
    }

    suspend fun removeSchedule(scheduleId: String) {
        val schedule = get(scheduleId)!!
        schedule.media?.run { removeScheduleMedia(this.objectId) }
        scheduleManager.remove(scheduleId)
    }

    private suspend fun removeScheduleMedia(scheduleId: String) {
        val filePath = "$BUCKET_SCHEDULE/$scheduleId"
        StorageManager.deleteFile(filePath)
    }

    suspend fun update(
        scope: CoroutineScope,
        schedule: Schedule,
        uploadingMedia: UploadingMedia?
    ) {
        schedule.media?.run {
            removeScheduleMedia(this.objectId)
            schedule.media = null
        }
        uploadingMedia?.run { schedule.media = uploadScheduleMedia(scope, this) }
        scheduleManager.set(schedule)
    }
}