package com.demo.fcmNotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.demo.R
import com.demo.constants.Const.EXTRA_FCM_TAG
import com.demo.ui.wallpaper.home.HomeActivity
import com.demo.utils.LogHelper
import com.demo.utils.PreferenceManager


/**
 * Notification type
 * 1. Chat
 * 2. ask_query
 * 3. sub_expire
 */
class MyFcmListenerService : FirebaseMessagingService() {
    private val TAG = javaClass.simpleName

    var data: Map<String, String>? = null
    var builder: NotificationCompat.Builder? = null
    var notification_type = 0
    var contentMessage = ""
    //var fcmNotificationModel: FcmNotificationModel? = null

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        val preferenceManager = PreferenceManager(this)
        preferenceManager.setStringPreference(PreferenceManager.FCM_TOKEN, newToken)
        LogHelper.e(TAG, "token=$newToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            data = message.data
            val preferenceManager = PreferenceManager(this)
            LogHelper.e("TAG", "FCM MSG ==" + data.toString())
            LogHelper.i("myTag$TAG", "Message Received")

            // if user not login then return from here
            if (!preferenceManager.getBooleanPreference(PreferenceManager.IS_USER_ALREADY_LOGIN)) {
                return
            }

            // get notification_type
            if (data!!.containsKey("notification_type")) {
                try {
                    notification_type = data!!["notification_type"].toString().toInt()
                } catch (e: Exception) {
                    e.message?.let { LogHelper.e(TAG, it) }
                }
            }
            if (data!!.containsKey("gcm_msg")) {
                contentMessage = data!!["gcm_msg"].toString()
                builder?.setContentText(contentMessage)
                builder?.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(data!!["gcm_msg"] as CharSequence?)
                )

            }

            var title = getString(R.string.app_name)
            if (data!!.containsKey("title")) {
                if (!TextUtils.isEmpty(title))
                    title = data!!["title"].toString()
            }

            if (!data.isNullOrEmpty()) {
                /*fcmNotificationModel = Gson().fromJson(data.toString(), FcmNotificationModel::class.java)
                LogHelper.d("fcmDataParse", fcmNotificationModel.toString())
                if (fcmNotificationModel!!.body.isNotEmpty()){
                    contentMessage = fcmNotificationModel!!.body[0].msg
                    notification_type = 0.takeIf { fcmNotificationModel!!.body[0].tag.isEmpty() } ?: fcmNotificationModel!!.body[0].tag.toInt()
                    LogHelper.d("fcmMessage", contentMessage.toString())
                    LogHelper.d("fcmType", notification_type.toString())
                }*/
            }

            val intent: Intent
            when (notification_type) {
                TYPE_ACCEPTED, TYPE_REJECTED -> {
                    intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra(EXTRA_FCM_TAG, 2)
                }
                TYPE_NOTIFICATION -> {
                    intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra(EXTRA_FCM_TAG, notification_type)
                }
                else -> {
                    intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
            }


            showNotification(this, title, contentMessage, intent)

        } catch (e: Exception) {
            LogHelper.printStackTrace(e)
            LogHelper.e(TAG, "error==" + e.message)
        }
    }

    private fun showNotification(context: Context, title: String?, body: String?, intent: Intent?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = "Notification"
        val importance = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            mChannel.enableVibration(true)
            mChannel.enableLights(true)
            mChannel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()
            )
            notificationManager.createNotificationChannel(mChannel)
        }
        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setColor(ContextCompat.getColor(context, R.color.primary))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(intent!!)

        stackBuilder.addParentStack(HomeActivity::class.java)
        // get notification id
        var id = PreferenceManager(this).getIntPreference(PreferenceManager.NOTIFICATION_ID)
        if (id >= Integer.MAX_VALUE)
            id = 0
        PreferenceManager(this).setIntPreference(PreferenceManager.NOTIFICATION_ID, id + 1)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            id,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        notificationManager.notify(notificationId, mBuilder.build())
    }


    companion object {
        private const val TYPE_ACCEPTED = 1
        private const val TYPE_REJECTED = 2
        private const val TYPE_NOTIFICATION = 3
    }

}
