package app.hankdev.firebase

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object MessagingManager {
    private val messaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance() }

    suspend fun getToken() = messaging.token.await()

    suspend fun subscribeToTopic(topic: String) = messaging.subscribeToTopic(topic).await()

    fun unsubscribeFromTopic(topic: String) = messaging.unsubscribeFromTopic(topic)
}