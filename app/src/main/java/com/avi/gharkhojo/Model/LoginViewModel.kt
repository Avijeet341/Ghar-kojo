package com.avi.gharkhojo.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun signInUser(email: String, pass: String) {
        if (email.isNotEmpty() && pass.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userData = user?.let {
                        UserData(it.uid, it.displayName, it.photoUrl?.toString())
                    }
                    _loginState.value = LoginState.Success(userData)
                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "Login failed.")
                }
            }
        } else {
            _loginState.value = LoginState.Error("Empty Fields Are Not Allowed!!😓.")
        }
    }

    fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                val userData = user?.let {
                    UserData(it.uid, it.displayName, it.photoUrl?.toString())
                }
                _loginState.value = LoginState.Success(userData)
            } else {
                _loginState.value = LoginState.Error("Google sign-in failed.")
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    sealed class LoginState {
        data class Success(val userData: UserData?) : LoginState()
        data class Error(val message: String) : LoginState()
        object Idle : LoginState()
    }
}
