package app.hankdev.firebase

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class MessagingManager(private val messaging: FirebaseMessaging) {

    suspend fun getToken() = messaging.token.await()

    suspend fun subscribeToTopic(topic: String) = messaging.subscribeToTopic(topic).await()

    fun unsubscribeFromTopic(topic: String) = messaging.unsubscribeFromTopic(topic)
}