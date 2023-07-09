package com.yamin.chatchat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yamin.chatchat.data.models.Friend
import com.yamin.chatchat.repositories.FriendsRepository
import com.yamin.chatchat.utils.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsViewModel : ViewModel() {
    private val friendsRepository = FriendsRepository()
    private var disposable = CompositeDisposable()

    private val _myFriendList = MutableLiveData<Response<ArrayList<Friend>>>()
    val myFriendList: LiveData<Response<ArrayList<Friend>>>
        get() = _myFriendList

    private val _availableUserList = MutableLiveData<Response<ArrayList<Friend>>>()
    val availableUserList: LiveData<Response<ArrayList<Friend>>>
        get() = _availableUserList

    private val _availableUserListChange = MutableLiveData<Response<Boolean>>()
    val availableUserListChange: LiveData<Response<Boolean>>
        get() = _availableUserListChange

    private val _myRequestsList = MutableLiveData<Response<ArrayList<Friend>>>()
    val myRequestsList: LiveData<Response<ArrayList<Friend>>>
        get() = _myRequestsList

    private val _sentRequestsList = MutableLiveData<Response<ArrayList<Friend>>>()
    val sentRequestsList: LiveData<Response<ArrayList<Friend>>>
        get() = _sentRequestsList

    private val _sendRequestStatus = MutableLiveData<Response<Boolean>>()
    val sendRequestStatus: LiveData<Response<Boolean>>
        get() = _sendRequestStatus

    private val _cancelRequestStatus = MutableLiveData<Response<Boolean>>()
    val cancelRequestStatus: LiveData<Response<Boolean>>
        get() = _cancelRequestStatus

    private val _acceptRequestStatus = MutableLiveData<Response<Boolean>>()
    val acceptRequestStatus: LiveData<Response<Boolean>>
        get() = _acceptRequestStatus

    init {
        observeSendRequestStatus()
        observeCancelRequestStatus()
        observeAcceptRequestStatus()
        observeMyFriendList()
        observeAvailableUserList()
        observeAvailableUserListChange()
        observeMyRequestsList()
        observeSentRequestsList()
        registerEventListenersForRealTimeDbChanges()
    }

    private fun registerEventListenersForRealTimeDbChanges() {
        Log.d(TAG, "registerEventListenersForRealTimeDbChanges")
        friendsRepository.registerEventChangeListeners()
    }

    private fun observeAcceptRequestStatus() {
        Log.d(TAG, "observeAcceptRequestStatus")
        disposable.add(friendsRepository.acceptRequestStatus
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeAcceptRequestStatus isOnError")
                    _acceptRequestStatus.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeAcceptRequestStatus isOnNext")
                    it.value?.let { result ->
                        _acceptRequestStatus.postValue(Response.Success(result))
                    }
                }
            })
    }

    private fun observeCancelRequestStatus() {
        Log.d(TAG, "observeCancelRequestStatus")
        disposable.add(friendsRepository.cancelRequestStatus
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeCancelRequestStatus isOnError")
                    _cancelRequestStatus.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeCancelRequestStatus isOnNext")
                    it.value?.let { result ->
                        _cancelRequestStatus.postValue(Response.Success(result))
                    }
                }
            })
    }

    private fun observeSendRequestStatus() {
        Log.d(TAG, "observeSendRequestStatus")
        disposable.add(friendsRepository.sendRequestStatus
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeSendRequestStatus isOnError")
                    _sendRequestStatus.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeSendRequestStatus isOnNext")
                    it.value?.let { result ->
                        _sendRequestStatus.postValue(Response.Success(result))
                    }
                }
            })
    }

    private fun observeSentRequestsList() {
        Log.d(TAG, "observeSentRequestsList")
        disposable.add(friendsRepository.sentRequestsList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeSentRequestsList isOnError")
                    _sentRequestsList.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeSentRequestsList isOnNext")
                    it.value?.let { result ->
                        _sentRequestsList.postValue(Response.Success(result))
                    }
                }
            })
    }

    private fun observeMyRequestsList() {
        Log.d(TAG, "observeMyRequestsList")
        disposable.add(friendsRepository.myRequestsList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeMyRequestsList isOnError")
                    _myRequestsList.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeMyRequestsList isOnNext")
                    it.value?.let { result ->
                        _myRequestsList.postValue(Response.Success(result))
                    }
                }
            })
    }

    private fun observeAvailableUserList() {
        Log.d(TAG, "observeUserList")

        disposable.add(friendsRepository.availableUserList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeUserList isOnError")
                    _availableUserList.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeUserList isOnNext")
                    it.value?.let { result ->
                        _availableUserList.postValue(Response.Success(result))
                    }
                }
            })
    }

    private fun observeAvailableUserListChange() {
        Log.d(TAG, "observeAvailableUserListChange")

        disposable.add(friendsRepository.availableUserListChange
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeAvailableUserListChange isOnError")
                    _availableUserListChange.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeAvailableUserListChange isOnError")
                    _availableUserListChange.postValue(Response.Success(it.value!!))
                }
            })
    }

    private fun observeMyFriendList() {
        Log.d(TAG, "observeMyFriendList")

        disposable.add(friendsRepository.myFriendList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnError) {
                    Log.d(TAG, "observeMyFriendList isOnError")
                    _myFriendList.postValue(Response.Error(it.error?.message!!))
                } else {
                    Log.d(TAG, "observeMyFriendList isOnNext")
                    it.value?.let { result ->
                        _myFriendList.postValue(Response.Success(result))
                    }
                }
            })
    }

    fun sendFriendRequest(receiverId: String) {
        Log.d(TAG, "sendFriendRequest")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    friendsRepository.sendRequest(receiverId)
                } catch (e: Exception) {
                    Log.d(TAG, "Error sending friend request", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun cancelFriendRequest(receiverId: String) {
        Log.d(TAG, "cancelFriendRequest")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    friendsRepository.cancelRequest(receiverId)
                } catch (e: Exception) {
                    Log.d(TAG, "Error cancel friend request", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun acceptFriendRequest(senderId: String) {
        Log.d(TAG, "acceptFriendRequest")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    friendsRepository.acceptRequest(senderId)
                } catch (e: Exception) {
                    Log.d(TAG, "Error accepting friend request", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun getAvailableUserList(): ArrayList<Friend> {
        Log.d(TAG, "getAvailableUserList")

        var userList = arrayListOf<Friend>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    userList = friendsRepository.getAllUsersList()
                } catch (e: Exception) {
                    Log.d(TAG, "Error getting all user list", e)
                    e.printStackTrace()
                }
            }
        }
        Log.d(TAG, "userList $userList")
        return userList
    }

    fun getMyRequestsList(): ArrayList<Friend> {
        Log.d(TAG, "getMyRequestsList")

        var requestsList = arrayListOf<Friend>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    requestsList = friendsRepository.getMyRequestsList()
                } catch (e: Exception) {
                    Log.d(TAG, "Error getting my requests list", e)
                    e.printStackTrace()
                }
            }
        }
        Log.d(TAG, "requestsList $requestsList")
        return requestsList
    }

    fun getSentRequestsList(): ArrayList<Friend> {
        Log.d(TAG, "getSentRequestsList")

        var sentRequestsList = arrayListOf<Friend>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    sentRequestsList = friendsRepository.getSentRequestsList()
                } catch (e: Exception) {
                    Log.d(TAG, "Error getting sent requests list", e)
                    e.printStackTrace()
                }
            }
        }
        Log.d(TAG, "sentRequestsList $sentRequestsList")
        return sentRequestsList
    }

    fun getMyFriendsList(): ArrayList<Friend> {
        Log.d(TAG, "getMyFriendsList")

        var myFriendsList = arrayListOf<Friend>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    myFriendsList = friendsRepository.getMyFriendsList()
                } catch (e: Exception) {
                    Log.d(TAG, "Error getting my friends list", e)
                    e.printStackTrace()
                }
            }
        }
        Log.d(TAG, "myFriendsList $myFriendsList")
        return myFriendsList
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared")
        super.onCleared()
        disposable.dispose()
        friendsRepository.removeAllEventListeners()
    }

    companion object {
        const val TAG = "FriendsViewModel"
    }
}