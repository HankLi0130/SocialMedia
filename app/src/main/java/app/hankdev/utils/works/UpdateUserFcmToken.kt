package app.hankdev.utils.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.hankdev.data.models.firestore.FirestoreModel
import app.hankdev.data.models.firestore.Installation
import app.hankdev.firestore.ProfileManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UpdateUserFcmToken(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams), KoinComponent {

    private val profileManager: ProfileManager by inject()

    override suspend fun doWork(): Result {
        val token = inputData.getString(Installation.FCM_TOKEN)
        val userId = inputData.getString(FirestoreModel.OBJECT_ID)

        return if (!token.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            try {

            } catch (e: Throwable) {
                Result.retry()
            }
            Result.success()
        } else Result.failure()
    }
}