package com.avi.gharkhojo.notifications

import android.content.Context
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.browser.trusted.Token
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.avi.gharkhojo.Chat.ChatRoom
import org.json.JSONObject

class SendNotification(
    var userFcmToken: String, var title: String, var message: String,
    var senderId: String,
    var senderPic:String,
    var imageUrl:String,
    var context: Context
) {


    @OptIn(UnstableApi::class)
    fun sendNotifications(){

        var requestQueue:RequestQueue = Volley.newRequestQueue(context)
        try {
            val mainObj = JSONObject()
            val messageObject = JSONObject()
            val notificationObject = JSONObject()
            val dataObject = JSONObject()

            notificationObject.put("title", title)
            notificationObject.put("body", message)

            dataObject.put("imageDataUrl",imageUrl)

            dataObject.put("image", senderPic)
            dataObject.put("uid", senderId)

            messageObject.put("token", userFcmToken)
            messageObject.put("notification", notificationObject)
            messageObject.put("data", dataObject)

            mainObj.put("message", messageObject)

            val request = object : JsonObjectRequest(
                Request.Method.POST, NotificationConstant.POST_URL.value, mainObj,
                Response.Listener { response ->
                    Log.d("SendNotification", "Success: $response")
                },
                Response.ErrorListener { error ->
                    Log.e("SendNotification", "Error: ${error.message}")
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                   var accessToken: AccessToken = AccessToken()
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer ${accessToken.getAccessToken()}"
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
            requestQueue.add(request)
        }catch (e: Exception){
            Toast.makeText(context,"Error: ${e.message}",Toast.LENGTH_SHORT).show()
        }


    }
}