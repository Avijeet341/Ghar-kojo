package com.avi.gharkhojo.Model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

sealed class SharedViewModel : ViewModel() {

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> get() = _userData

    fun setUserData(userData: UserData) {
        _userData.value = userData
        Log.d("SharedViewModel", "User data set: $userData")
    }
}
