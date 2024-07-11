package com.avi.gharkhojo.Model



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignUpViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _signUpState = MutableLiveData<SignUpState>()
    val signUpState: LiveData<SignUpState> = _signUpState

    fun signUpWithFirebase(email: String, pass: String, confirmPass: String) {
        if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
            if (pass == confirmPass) {
                _signUpState.value = SignUpState.Loading
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _signUpState.value = SignUpState.Success

                    } else {
                        _signUpState.value = SignUpState.Error(task.exception?.message ?: "Sign-up failed.")
                    }
                }
            } else {
                _signUpState.value = SignUpState.Error("Password is not matching 😫.")
            }
        } else {
            _signUpState.value = SignUpState.Error("Empty Fields Are Not Allowed!!😓.")
        }
    }

    sealed class SignUpState {
        object Loading : SignUpState()
        object Success : SignUpState()
        data class Error(val message: String) : SignUpState()
        object Idle : SignUpState()
    }
}
