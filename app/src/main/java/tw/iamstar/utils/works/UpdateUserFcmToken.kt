package tw.iamstar.utils.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tw.iamstar.data.models.FirestoreModel
import tw.iamstar.data.models.Installation
import tw.iamstar.firestore.ProfileManager

@KoinApiExtension
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