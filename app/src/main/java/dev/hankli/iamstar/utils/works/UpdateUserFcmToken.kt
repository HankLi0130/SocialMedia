package dev.hankli.iamstar.utils.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.repo.ProfileRepo
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class UpdateUserFcmToken(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams), KoinComponent {

    private val profileRepo: ProfileRepo by inject()

    override suspend fun doWork(): Result {
        val token = inputData.getString(Profile.FCM_TOKEN)

        // TODO update FCM token of profile

        return Result.success()
    }
}