package com.avi.gharkhojo.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Chat.Chat_Activity
import com.avi.gharkhojo.MainActivity
import com.avi.gharkhojo.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val uid = data["uid"]
        val name = message.notification?.title
        val img = data["image"]

        var notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var r: Ringtone? = RingtoneManager.getRingtone(applicationContext,notification)
        r?.play()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            r?.setLooping(false)
        }
        var v: Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        var pattern: LongArray = longArrayOf(100,300,300,300)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this,
            NotificationConstant.CHANNEL_ID.value)
        builder.setSmallIcon(R.drawable.baseline_person_24)

        var resultIntent: Intent = Intent(this, ChatRoom::class.java).apply {
            putExtra(ChatRoom.UID_ARG, uid)
            putExtra(ChatRoom.NAME_ARG, name)
            putExtra(ChatRoom.IMG_ARG, img)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        var pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        Glide.with(applicationContext)
            .asBitmap()
            .load(message.data["imageDataUrl"])
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    builder.setLargeIcon(resource)
                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }

            })
        builder.setContentIntent(pendingIntent)
        builder.setContentTitle(message.notification?.title)
        builder.setContentText(message.notification?.body)
        builder.setAutoCancel(true)
        builder.setSound(notification)
        builder.setVibrate(pattern)
        builder.setPriority(NotificationManager.IMPORTANCE_HIGH)

        var mNotificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var channel: NotificationChannel = NotificationChannel(NotificationConstant.CHANNEL_ID.value,
                NotificationConstant.NOTIFICATION_NAME.value,NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            builder.setChannelId(NotificationConstant.CHANNEL_ID.value)

        }
        mNotificationManager.notify(1,builder.build())


    }

    
}