package com.avi.gharkhojo.Model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avi.gharkhojo.R
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun signInUser(email: String, pass: String,context:Context) {
        if (email.isNotEmpty() && pass.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    UserSignupLoginManager.getInstance(context).setUp()
                    _loginState.postValue(LoginState.Success(UserData))
                } else {
                    _loginState.postValue(LoginState.Error(task.exception?.message ?: "Login failed."))
                }
            }
        } else {
            _loginState.postValue(LoginState.Error("Empty Fields Are Not Allowed!!😓."))
        }
    }

    fun firebaseGoogleAccount(idToken: String,context: Context) {
        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth
                user?.let {
                    user->
                   isUserExist {
                       if(!it){
                          setUserData(context)
                       }
                       else{
                           _loginState.postValue(LoginState.Error("User Already Exist With this email."))
                           user.signOut()
                       }
                   }
                }

            } else {
                _loginState.postValue(LoginState.Error("Google sign-in failed."))
            }
        }
    }

    fun isUserExist(onComplete: (Boolean) -> Unit) {
        databaseReference.child("users").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null) {
                    var isExist = false
                    for (child in snapshot.children) {
                        val userEmail = child.child("userEmail").getValue(String::class.java)
                        val userId = child.child("userId").getValue(String::class.java)
                        if (userEmail == firebaseAuth.currentUser?.email || userId == firebaseAuth.currentUser?.uid) {
                            isExist = true
                            break
                        }
                    }
                    onComplete(isExist)
                } else {
                    onComplete(false)
                }
            } else {
                onComplete(false)
            }
        }


    }

    fun setUserData(context:Context){
        var profilePic:String = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()


        profilePic = if(profilePic=="null")"" else profilePic
        var userName:String = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        var userId:String? = FirebaseAuth.getInstance().currentUser?.uid

        databaseReference.child("users").push().setValue(ChatUserListModel(userName,profilePic,userId,FirebaseAuth.getInstance().currentUser?.email))
        if (userId != null) {
            val userData = mapOf(
                "name" to userName,
                "address" to "",
                "phone" to ""
            )
            FirebaseFirestore.getInstance().collection("users").document(userId).set(userData)
            UserSignupLoginManager.getInstance(context).setUp()
            _loginState.postValue(LoginState.Success(UserData))
        }


    }

    sealed class LoginState {
        data class Success(val userData: UserData?) : LoginState()
        data class Error(val message: String) : LoginState()
        object Idle : LoginState()
    }
}