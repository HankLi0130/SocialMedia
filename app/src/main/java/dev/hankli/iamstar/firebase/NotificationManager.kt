package dev.hankli.iamstar.firebase

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging
import dev.hankli.iamstar.data.models.FirestoreModel
import dev.hankli.iamstar.data.models.Profile
import dev.hankli.iamstar.utils.SharedPreferencesManager
import dev.hankli.iamstar.utils.works.UpdateUserFcmToken
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinApiExtension


class NotificationManager(
    private val messaging: FirebaseMessaging,
    private val spManager: SharedPreferencesManager
) {

    fun saveFcmToken(token: String) = spManager.saveFcmToken(token)

    suspend fun getCurrentToken() = messaging.token.await()

    @KoinApiExtension
    fun uploadFcmToken(context: Context, owner: LifecycleOwner) {
        AuthManager.currentUserId?.let { userId ->
            if (spManager.fcmTokenExists()) {
                val inputData = Data.Builder()
                    .putString(Profile.FCM_TOKEN, spManager.getFcmToken())
                    .putString(FirestoreModel.OBJECT_ID, userId)
                    .build()

                val updateFcmTokenRequest = OneTimeWorkRequestBuilder<UpdateUserFcmToken>()
                    .setInputData(inputData)
                    .build()

                val workManager = WorkManager.getInstance(context)
                workManager.enqueue(updateFcmTokenRequest)

                workManager.getWorkInfoByIdLiveData(updateFcmTokenRequest.id)
                    .observe(owner) { workInfo ->
                        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            spManager.remoeFcmToken()
                        }
                    }
            }
        }
    }
}