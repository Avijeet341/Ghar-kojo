package com.avi.gharkhojo.Model



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avi.gharkhojo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _signUpState = MutableLiveData<SignUpState>()
    val signUpState: LiveData<SignUpState> = _signUpState

    fun signUpWithFirebase(name:String,email: String, pass: String, confirmPass: String) {
        if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
            if (pass == confirmPass) {
                _signUpState.value = SignUpState.Loading
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _signUpState.value = SignUpState.Success
                        UserData.username = name
                        UserData.email = email

                        var profilePic:String = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()


                        profilePic = if(profilePic=="null")"" else profilePic
                        var userName:String = name
                        var userId:String? = FirebaseAuth.getInstance().currentUser?.uid

                        UserData.username = name
                        databaseReference.child("users").push().setValue(ChatUserListModel(userName,profilePic,userId))
                        if (userId != null) {
                            val userData = mapOf(
                                "name" to userName,
                                "address" to "",
                                "phone" to ""
                            )
                            firestore.collection("users").document(userId).set(userData)
                        }

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
