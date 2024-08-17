package com.avi.gharkhojo.dagger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DependencyManager {
    private val CHANNEL_ID = "message_notification_id"
    private val CHANNEL_NAME = "message_notification"
    @Provides
    @Singleton
   fun provideNotificationManager(@ActivityContext context: Context):NotificationManagerCompat {
       var notificationManager =  NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel =  NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return notificationManager
    }
    @Provides
    @Singleton
    fun provideGlide(@ApplicationContext context: Context) = Glide.with(context)


}