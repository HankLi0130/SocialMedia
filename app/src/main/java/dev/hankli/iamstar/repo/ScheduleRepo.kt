package dev.hankli.iamstar.repo

import dev.hankli.iamstar.data.models.Schedule
import dev.hankli.iamstar.firebase.BUCKET_SCHEDULE
import dev.hankli.iamstar.firebase.StorageManager
import dev.hankli.iamstar.firestore.InfluencerManager
import dev.hankli.iamstar.firestore.ProfileManager
import dev.hankli.iamstar.firestore.ScheduleManager
import dev.hankli.iamstar.utils.media.UploadingMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

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
            schedule.photoURL = uploadScheduleMedia(coroutineScope, this)
        }
        scheduleManager.add(schedule)
    }

    private suspend fun uploadScheduleMedia(scope: CoroutineScope, media: UploadingMedia): String {
        val filePath = "$BUCKET_SCHEDULE/${media.objectId}"
        return withContext(scope.coroutineContext) {
            StorageManager.uploadFile(filePath, media.file)
        }
    }
}