package tw.iamstar.firebase

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object MessagingManager {
    private val messaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance() }

    suspend fun getToken() = messaging.token.await()
}