package com.avi.gharkhojo.Model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avi.gharkhojo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val databaseReference: DatabaseReference by lazy { firebaseDatabase.reference }

    private val _loginState = SingleLiveEvent<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun signInUser(email: String, pass: String, context: Context) {
        if (email.isNotEmpty() && pass.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            firebaseAuth.currentUser?.reload()?.await()
                            UserSignupLoginManager.getInstance(context).setUp()
                        }
                        _loginState.postValue(LoginState.Success(UserData))
                    }
                } else {
                    _loginState.postValue(LoginState.Error(task.exception?.message ?: "Login failed."))
                }
            }
        } else {
            _loginState.postValue(LoginState.Error("Empty Fields Are Not Allowed!!😓."))
        }
    }

    fun firebaseGoogleAccount(idToken: String, context: Context) {
        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let {
                    viewModelScope.launch {
                        val exists = isUserExist()
                        if (!exists) {
                            setUserData(context)
                        } else {
                            _loginState.postValue(LoginState.Error("User Already Exists With this email."))
                            firebaseAuth.signOut()
                        }
                    }
                }
            } else {
                _loginState.postValue(LoginState.Error("Google sign-in failed."))
            }
        }
    }

    private suspend fun isUserExist(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = databaseReference.child("users").get().await()
                var isExist = false
                for (child in snapshot.children) {
                    val userEmail = child.child("userEmail").getValue(String::class.java)
                    val userId = child.child("userId").getValue(String::class.java)
                    if (userEmail == firebaseAuth.currentUser?.email || userId == firebaseAuth.currentUser?.uid) {
                        isExist = true
                        break
                    }
                }
                isExist
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun setUserData(context: Context) {
        viewModelScope.launch {
            firebaseAuth.currentUser?.reload()?.await()
            val profilePic = FirebaseAuth.getInstance().currentUser?.photoUrl.toString().takeIf { it != "null" } ?: ""
            val userName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            databaseReference.child("users").push().setValue(ChatUserListModel(userName, profilePic, userId, FirebaseAuth.getInstance().currentUser?.email))
            if (userId != null) {
                val userData = mapOf(
                    "name" to userName,
                    "address" to "",
                    "phone" to ""
                )
                Toast.makeText(context, "Welcome $userName", Toast.LENGTH_SHORT).show()
                FirebaseFirestore.getInstance().collection("users").document(userId).set(userData).await()
                async {
                    UserSignupLoginManager.getInstance(context).setUp()
                }.await()
                _loginState.postValue(LoginState.Success(UserData))
            }
        }
    }

    sealed class LoginState {
        data class Success(val userData: UserData?) : LoginState()
        data class Error(val message: String) : LoginState()
        object Idle : LoginState()
    }
}
