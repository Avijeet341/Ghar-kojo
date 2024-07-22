package com.avi.gharkhojo.Chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avi.gharkhojo.Adapter.ChatAdapter
import com.avi.gharkhojo.Model.ChatRoomModel

class ChatLiveData(private val chatAdapter: ChatAdapter): ViewModel() {


   private var liveData = MutableLiveData<List<ChatRoomModel>>()

   fun getLiveData(): LiveData<List<ChatRoomModel>> {
      return liveData
   }

   fun setLiveData(chatList:List<ChatRoomModel>):Unit{
      liveData.value = chatList
   }
}