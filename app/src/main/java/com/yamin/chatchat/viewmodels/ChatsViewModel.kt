package com.yamin.chatchat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yamin.chatchat.data.models.Chats
import com.yamin.chatchat.data.models.Message
import com.yamin.chatchat.repositories.ChatsRepository
import com.yamin.chatchat.utils.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatsViewModel : ViewModel() {
    private val chatsRepository = ChatsRepository()
    private var disposable = CompositeDisposable()

    private val _chatsList = MutableLiveData<Response<ArrayList<Chats>>>()
    val chatsList: LiveData<Response<ArrayList<Chats>>>
        get() = _chatsList

    init {
        observeChatsList()
    }

    fun sendMessageToReceiver(message: String, receiverId: String, timestamp: Long) {
        Log.d(TAG, "sendMessageToReceiver")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    chatsRepository.sendMessage(message, receiverId, timestamp)
                } catch (e: Exception) {
                    Log.d(TAG, "Error sending message to receiver", e)
                    e.printStackTrace()
                }
            }
        }
    }

    private fun observeChatsList() {
        Log.d(TAG, "observeChatsList")
        disposable.add(chatsRepository.chatsList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeChatsList isOnError")
                    _chatsList.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeChatsList isOnNext")
                    it.value?.let { result ->
                        _chatsList.postValue(Response.Success(result))
                    }
                }
            })
    }

    fun activateLiveMessageObserver(receiverId: String) {
        Log.d(TAG, "activateLiveMessageObserver")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    chatsRepository.observeLiveMessages(receiverId)
                } catch (e: Exception) {
                    Log.d(TAG, "Error activating LiveMessageObserver", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun getLiveMessage(): LiveData<Response<Message>> {
        Log.d(TAG, "getLiveMessage")
        val liveMessage = MutableLiveData<Response<Message>>()
        disposable.add(chatsRepository.liveMessage
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "getLiveMessage isOnError")
                    liveMessage.postValue(Response.Error(it.error?.message!!))
                    return@subscribe
                } else {
                    Log.d(TAG, "getLiveMessage isOnNext")
                    it.value?.let { result ->
                        liveMessage.postValue(Response.Success(result))
                        return@subscribe
                    }
                }
            })
        return liveMessage
    }

    fun getConversationHistory(friendId: String): ArrayList<Message> {
        Log.d(TAG, "getConversationHistory")
        var fullConversation = arrayListOf<Message>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    fullConversation = chatsRepository.getFullConversation(friendId)
                } catch (e: Exception) {
                    Log.d(TAG, "Error updating conversation", e)
                    e.printStackTrace()
                }
            }
        }
        Log.d(TAG, "fullConversation $fullConversation")
        return fullConversation
    }

    fun getUserChatsList(): ArrayList<Chats> {
        Log.d(TAG, "getUserChatsList")
        var chatList = arrayListOf<Chats>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    chatList = chatsRepository.getChatsList()
                } catch (e: Exception) {
                    Log.d(TAG, "Error getting chats list", e)
                    e.printStackTrace()
                }
            }
        }
        Log.d(TAG, "chatList $chatList")
        return chatList
    }

    fun getCurrentUserId(): String? {
        Log.d(TAG, "getCurrentUserId")
        return chatsRepository.getCurrentUserId()
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared")
        super.onCleared()
        disposable.dispose()
    }

    companion object {
        const val TAG = "ChatsViewModel"
    }
}