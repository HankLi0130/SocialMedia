package dev.hankli.iamstar.firebase

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object NotificationManager {

    private val messaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance() }

    suspend fun getCurrentToken() = messaging.token.await()


}