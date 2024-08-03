package com.avi.gharkhojo.Model

import android.content.Context
import android.widget.Toast
import com.avi.gharkhojo.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class UserSignupLoginManager(private val context: Context) {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    lateinit var storageRef: StorageReference

   companion object {
        var instance: UserSignupLoginManager? = null
       fun getInstance(context: Context): UserSignupLoginManager {
           if (instance == null) {
               instance = UserSignupLoginManager(context).also {
                   it.storageRef = Firebase.storage.reference.child("profile_pictures/${FirebaseAuth.getInstance().currentUser?.uid}")
               }

           }
           return instance!!
       }
   }
    fun setUp(){

        UserData.email = if(UserData.email.isNullOrEmpty()){
            FirebaseAuth.getInstance().currentUser?.email
        }
        else{
            UserData.email
        }
        UserData.profilePictureUrl = if(UserData.profilePictureUrl.isNullOrEmpty()) {
            FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        }else{
            UserData.profilePictureUrl
        }
        UserData.username = if(UserData.username.isNullOrEmpty()){
            FirebaseAuth.getInstance().currentUser?.displayName

        }else{
            UserData.username
        }
        UserData.uid = FirebaseAuth.getInstance().currentUser?.uid



        storageRef.downloadUrl.addOnSuccessListener {

            if( it!=null && it.toString().isNotEmpty()) {
                UserData.profilePictureUrl = it.toString()

            }


        }.addOnFailureListener{
            UserData.profilePictureUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        }
        if(UserData.username.isNullOrEmpty() || UserData.address.isNullOrEmpty() || UserData.phn_no.isNullOrEmpty()) {
            firestore.collection("users").document(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: "").get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if(document.getString("name")!=null){
                            UserData.username = document.getString("name")
                        }
                        UserData.address = document.getString("address") ?: context.getString( R.string.default_address)
                        UserData.phn_no = document.getString("phone") ?: context.getString(R.string.default_phone)
                    }
                }
                .addOnFailureListener {
                    UserData.address = context.getString(R.string.default_address)
                    UserData.phn_no = context.getString(R.string.default_phone)
                }
        }
    }
}