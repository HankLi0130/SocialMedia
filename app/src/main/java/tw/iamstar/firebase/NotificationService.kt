package tw.iamstar.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent

@KoinApiExtension
class NotificationService : FirebaseMessagingService(), KoinComponent {

    private val TAG = "HankTest"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO save fcm token
    }

    // https://firebase.google.com/docs/cloud-messaging/android/receive#override-onmessagereceived
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "From: ${message.from}")

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")
        }

        // Check if message contains a notification payload.
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }
}