package com.avi.gharkhojo.Model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

 class DataSharing:ViewModel(){
     var searchedData = MutableLiveData<List<Post>>()
     var searchedText = MutableLiveData<String>()
 }
