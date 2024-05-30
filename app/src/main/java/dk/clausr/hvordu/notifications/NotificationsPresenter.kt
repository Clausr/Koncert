package dk.clausr.hvordu.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import dk.clausr.hvordu.R
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


private const val DEEP_LINK_SCHEME_AND_HOST = "https://hvordu.clausr.dk"
private const val MESSAGE_NOTIFICATION_REQUEST_CODE = 80085

@Singleton
class NotificationsPresenter @Inject constructor(
    private val applicationContext: Context
) {
    private val context: Context
        get() = ContextThemeWrapper(applicationContext, R.style.Theme_Hvordu)

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun showNotification(
        title: String,
        contentText: String,
        tag: String?,
        id: Int,
        notificationChannel: HvorduNotificationChannel,
        chatRoomId: String?,
    ) {
        val intent = messagePendingIntent(chatRoomId)
        Timber.d("Show notification: $title - $contentText .. intent $intent")
        val notification = NotificationCompat.Builder(context, notificationChannel.channelId)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(intent)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setChannelId(notificationChannel.channelId)
            .build()

        notificationManager.notify(tag, id, notification)
    }

    fun registerChannels() {
        HvorduNotificationChannel
            .entries
            .forEach { hvorduChannel ->
                notificationManager.createNotificationChannel(getNotificationChannel(hvorduChannel))
            }
    }

    private fun messagePendingIntent(chatRoomId: String?): PendingIntent? =
        chatRoomId?.let { chatId ->
            Timber.d("messagePendingIntent $chatRoomId")
            PendingIntent.getActivity(
                context,
                MESSAGE_NOTIFICATION_REQUEST_CODE,
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = "$DEEP_LINK_SCHEME_AND_HOST/chatroom/$chatId".toUri()
                    component = ComponentName(
                        context.packageName,
                        "dk.clausr.hvordu.MainActivity"
                    )
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

    private fun getNotificationChannel(hvorduNotificationChannel: HvorduNotificationChannel): NotificationChannel {
        return NotificationChannel(
            hvorduNotificationChannel.channelId,
            context.getString(hvorduNotificationChannel.channelName),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(hvorduNotificationChannel.channelDescription)
            enableLights(true)
            enableVibration(true)
        }
    }
}