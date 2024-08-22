package com.avi.gharkhojo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Random

class NotificationServices:Service() {

    private val CHANNEL_ID = "message_notification_id"
    private val CHANNEL_NAME = "message_notification"
    lateinit var notificationCompat: NotificationCompat.Builder
    lateinit var notificationManager: NotificationManager
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var databaseReference: DatabaseReference = firebaseDatabase.reference
    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel: NotificationChannel = NotificationChannel( CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    description = "Message Notification"
                }
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        if(intent!=null){
            val title = intent.getStringExtra("title")
            val message = intent.getStringExtra("message")
            val roomId = intent.getStringExtra("roomId")
            val senderId = intent.getStringExtra("senderId")
            sendNotification(title!!,message!!,roomId!!,senderId!!)
        }

        return START_STICKY
    }

    private fun sendNotification(title: String, message: String,roomId:String,senderId:String){
        databaseReference.child("Presence").child(roomId!!)
            .child(senderId!!).get().addOnSuccessListener {
                val presence = it.getValue(String::class.java)
                if(presence!!.isNotEmpty() && presence=="Offline"){
                    notificationCompat = NotificationCompat.Builder(this,CHANNEL_ID)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentTitle("${title}")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.vibe)
                    with(NotificationManagerCompat.from(this)){
                        if (ActivityCompat.checkSelfPermission(
                                this@NotificationServices,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return@with
                        }
//                        notify(Random().nextInt(),notificationCompat.build())
                        startForeground(Random().nextInt(),notificationCompat.build())
                    }

                }
            }
    }

}