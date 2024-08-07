package com.avi.gharkhojo.Model

import android.content.Context
import com.avi.gharkhojo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserSignupLoginManager(private val context: Context) {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var storageRef: StorageReference

    companion object {
        var instance: UserSignupLoginManager? = null

        fun getInstance(context: Context): UserSignupLoginManager {
            if (instance == null) {
                instance = UserSignupLoginManager(context).also {
                    it.storageRef = FirebaseStorage.getInstance().reference.child(
                        "profile_pictures/${FirebaseAuth.getInstance().currentUser?.uid}"
                    )
                }
            }
            return instance!!
        }
    }

    suspend fun setUp() = withContext(Dispatchers.IO) {
        setUpData()
    }

    private suspend fun setUpData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            UserData.email = user.email ?: UserData.email
            UserData.profilePictureUrl = fetchProfilePictureUrl(user)
            UserData.username = user.displayName ?: UserData.username
            UserData.uid = user.uid

            if (UserData.username.isNullOrEmpty() || UserData.address.isNullOrEmpty() || UserData.phn_no.isNullOrEmpty()) {
                fetchAdditionalUserData(user.uid)
            }
        }
    }

    private suspend fun fetchProfilePictureUrl(user: FirebaseUser): String? {
        return try {
            val url = storageRef.downloadUrl.await()
            url?.toString().takeIf { it!!.isNotEmpty() } ?: user.photoUrl?.toString()
        } catch (e: Exception) {
            user.photoUrl?.toString().takeIf { it != "null" }
        }
    }

    private suspend fun fetchAdditionalUserData(uid: String) {
        try {
            val document = firestore.collection("users").document(uid).get().await()
            UserData.username = document.getString("name") ?: UserData.username
            UserData.address = document.getString("address") ?: context.getString(R.string.default_address)
            UserData.phn_no = document.getString("phone") ?: context.getString(R.string.default_phone)
        } catch (e: Exception) {
            UserData.address = context.getString(R.string.default_address)
            UserData.phn_no = context.getString(R.string.default_phone)
        }
    }
}
