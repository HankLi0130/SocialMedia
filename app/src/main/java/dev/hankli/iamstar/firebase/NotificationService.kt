package dev.hankli.iamstar.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import dev.hankli.iamstar.utils.SharedPreferencesManager
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class NotificationService : FirebaseMessagingService(), KoinComponent {

    private val spManager: SharedPreferencesManager by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        spManager.saveFcmToken(token)
    }
}