package dev.hankli.iamstar.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class NotificationService : FirebaseMessagingService(), KoinComponent {

    private val notificationManager: NotificationManager by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        notificationManager.saveFcmToken(token)
    }
}