package dev.hankli.iamstar.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import dev.hankli.iamstar.utils.SharedPreferencesManager
import kotlinx.coroutines.tasks.await


class NotificationManager(
    private val messaging: FirebaseMessaging,
    private val spManager: SharedPreferencesManager
) {

    fun saveFcmToken(token: String) = spManager.saveFcmToken(token)

    suspend fun getCurrentToken() = messaging.token.await()

    fun checkFcmToken() {
        if (spManager.fcmTokenExists()) {
            Log.i("HankLog", "fcm token is ")
        }
    }
}