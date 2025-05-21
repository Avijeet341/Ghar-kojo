package com.avi.gharkhojo.Model



import android.content.Context
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avi.gharkhojo.Adapter.CustomDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

     val _signUpState = MutableLiveData<SignUpState>()
    val signUpState: LiveData<SignUpState> = _signUpState

    fun signUpWithFirebase(
        name: String,
        email: String,
        pass: String,
        confirmPass: String,
        context: Context,
        button: Button,
        fragmentManager: FragmentManager
    ) {
        if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
            if (pass == confirmPass) {

                _signUpState.value = SignUpState.Loading

                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                      if (task.isSuccessful) {
                          firebaseAuth.currentUser?.sendEmailVerification()
                              ?.addOnSuccessListener {
                                  CustomDialog(context,firebaseAuth.currentUser!!,button,name,this@SignUpViewModel)
                                      .show(fragmentManager, "signUp")
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

    fun setUpUserData(name: String, email: String) {
        _signUpState.value = SignUpState.Success
        UserData.username = name
        UserData.email = email

        var profilePic:String = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()


        profilePic = if(profilePic=="null")"" else profilePic
        var userName:String = name
        var userId:String? = FirebaseAuth.getInstance().currentUser?.uid

        UserData.username = name

        CoroutineScope(Dispatchers.IO).launch {
            if(!isUserExist()){
                databaseReference.child("users").push().setValue(ChatUserListModel(userName,profilePic,userId,FirebaseAuth.getInstance().currentUser?.email)).await()
                if (userId != null) {
                    val userData = mapOf(
                        "username" to userName,
                        "address" to "",
                        "phn_no" to ""
                    )
                    firestore.collection("users").document(userId).set(userData).await()
                }
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

    sealed class SignUpState {
        object Loading : SignUpState()
        object Success : SignUpState()
        object VerificationFailure:SignUpState()
        data class Error(val message: String) : SignUpState()
        object Idle : SignUpState()
    }
}
