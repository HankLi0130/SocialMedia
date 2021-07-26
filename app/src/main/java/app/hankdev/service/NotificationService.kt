package app.hankdev.service

import android.app.Notification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import app.hankdev.BuildConfig
import app.hankdev.R
import app.hankdev.ui.MainActivity
import app.hankdev.utils.Consts.CHANNEL_ID
import app.hankdev.utils.Consts.MESSAGING_KEY
import app.hankdev.utils.Consts.MESSAGING_VALUE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.moshi.Moshi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationService : FirebaseMessagingService(), KoinComponent {

    private val TAG = "HankTest"
    private val topic = "/topics/${BuildConfig.APPLICATION_ID}"
    private val moshi: Moshi by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO save fcm token
    }

    // https://firebase.google.com/docs/cloud-messaging/android/receive#override-onmessagereceived
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "From: ${message.from}")
        Log.d(TAG, "Message data payload: ${message.data}")

        // From Topic
        if (topic == message.from && message.data.isNotEmpty()) {
            val key = message.data[MESSAGING_KEY]
            val value = message.data[MESSAGING_VALUE]
            if (key != null && value != null) {
                handleTopicMessaging(key, value)?.run {
                    val notification = createNotification(this)
                    showNotification(notification)
                }
            }
        }
    }

    private fun handleTopicMessaging(key: String, value: String): app.hankdev.data.models.messaging.MessagingData? {
        return when (app.hankdev.data.models.messaging.MessagingKey.valueOf(key)) {
            app.hankdev.data.models.messaging.MessagingKey.KEY_FEED -> moshi.adapter(app.hankdev.data.models.messaging.FeedData::class.java).fromJson(value)
        }
    }

    private fun createNotification(messagingData: app.hankdev.data.models.messaging.MessagingData): Notification {
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(messagingData.getDestId())
            .setArguments(messagingData.getArgs())
            .createPendingIntent()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(messagingData.getTitleResId()))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
    }

    private fun showNotification(notification: Notification) {
        // TODO notificationId is a unique int for each notification that you must define
        NotificationManagerCompat.from(this).notify(0, notification)
    }
}