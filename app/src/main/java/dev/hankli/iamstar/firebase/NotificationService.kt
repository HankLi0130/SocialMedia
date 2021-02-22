package dev.hankli.iamstar.firebase

import com.google.firebase.messaging.FirebaseMessagingService

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }
}