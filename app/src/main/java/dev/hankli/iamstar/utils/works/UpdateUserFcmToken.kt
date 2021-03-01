package dev.hankli.iamstar.utils.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.hankli.iamstar.data.models.FirestoreModel
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.firestore.ProfileManager
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class UpdateUserFcmToken(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams), KoinComponent {

    private val profileManager: ProfileManager by inject()

    override suspend fun doWork(): Result {
        val token = inputData.getString(Profile.FCM_TOKEN)
        val userId = inputData.getString(FirestoreModel.OBJECT_ID)

        return if (!token.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            try {
                profileManager.updateFcmToken(userId, token)
            } catch (e: Throwable) {
                Result.retry()
            }
            Result.success()
        } else Result.failure()
    }
}