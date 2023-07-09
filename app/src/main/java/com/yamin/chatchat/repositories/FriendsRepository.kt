package com.yamin.chatchat.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.ktx.Firebase
import com.yamin.chatchat.data.models.Friend
import io.reactivex.rxjava3.core.Notification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.tasks.await

class FriendsRepository {

    private val mAuth = Firebase.auth
    private val availableUsersDbRef = FirebaseDatabase.getInstance().getReference("users_public")
    private val friendListDbRef = FirebaseDatabase.getInstance().getReference("friends")
    private val sentRequestDbRef = FirebaseDatabase.getInstance().getReference("sent_requests")
    private val receivedRequestDbRef = FirebaseDatabase.getInstance().getReference("received_requests")

    private val _myFriendList = PublishSubject.create<Notification<ArrayList<Friend>>>()
    val myFriendList: Observable<Notification<ArrayList<Friend>>>
        get() = _myFriendList

    private val _currentMyFriendList = MutableLiveData<ArrayList<Friend>>()
    val currentMyFriendList: LiveData<ArrayList<Friend>>
        get() = _currentMyFriendList

    private val _availableUserList = PublishSubject.create<Notification<ArrayList<Friend>>>()
    val availableUserList: Observable<Notification<ArrayList<Friend>>>
        get() = _availableUserList

    private val _availableUserListChange = PublishSubject.create<Notification<Boolean>>()
    val availableUserListChange: Observable<Notification<Boolean>>
        get() = _availableUserListChange

    private val _currentAvailableUserList = MutableLiveData<ArrayList<Friend>>()
    val currentAvailableUserList: LiveData<ArrayList<Friend>>
        get() = _currentAvailableUserList

    private val _myRequestsList = PublishSubject.create<Notification<ArrayList<Friend>>>()
    val myRequestsList: Observable<Notification<ArrayList<Friend>>>
        get() = _myRequestsList

    private val _currentMyRequestsList = MutableLiveData<ArrayList<Friend>>()
    val currentMyRequestsList: LiveData<ArrayList<Friend>>
        get() = _currentMyRequestsList

    private val _sentRequestsList = PublishSubject.create<Notification<ArrayList<Friend>>>()
    val sentRequestsList: Observable<Notification<ArrayList<Friend>>>
        get() = _sentRequestsList

    private val _currentSentRequestsList = MutableLiveData<ArrayList<Friend>>()
    val currentSentRequestsList: LiveData<ArrayList<Friend>>
        get() = _currentSentRequestsList

    private val _sendRequestStatus = PublishSubject.create<Notification<Boolean>>()
    val sendRequestStatus: Observable<Notification<Boolean>>
        get() = _sendRequestStatus

    private val _cancelRequestStatus = PublishSubject.create<Notification<Boolean>>()
    val cancelRequestStatus: Observable<Notification<Boolean>>
        get() = _cancelRequestStatus

    private val _acceptRequestStatus = PublishSubject.create<Notification<Boolean>>()
    val acceptRequestStatus: Observable<Notification<Boolean>>
        get() = _acceptRequestStatus

    suspend fun sendRequest(receiverId: String) {
        Log.d(TAG, "sendRequest")
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val isSent = sentRequestDbRef.child(currentUserId).child(receiverId).get().await()
                val isReceived = receivedRequestDbRef.child(receiverId).child(currentUserId).get().await()
                if (!isSent.exists() && !isReceived.exists()) {
                    val snapshot = availableUsersDbRef.get().await()
                    for (childSnapshot in snapshot.children) {
                        val availableUser = childSnapshot.getValue(Friend::class.java)
                        availableUser?.apply {
                            if (id == currentUserId) {
                                val senderInfo = Friend(currentUserId, firstName, lastName, profileImage, email, mobile)
                                receivedRequestDbRef.child(receiverId).child(currentUserId).setValue(senderInfo).await()
                            } else if (id == receiverId) {
                                val receiverInfo = Friend(receiverId, firstName, lastName, profileImage, email, mobile)
                                sentRequestDbRef.child(currentUserId).child(receiverId).setValue(receiverInfo).await()
                            }
                        }
                    }
                    _sendRequestStatus.onNext(Notification.createOnNext(true))
                } else {
                    _sendRequestStatus.onNext(Notification.createOnNext(false))
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error sending friend request", e)
            _sendRequestStatus.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
    }

    suspend fun cancelRequest(receiverId: String) {
        Log.d(TAG, "cancelRequest")
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val isSent = sentRequestDbRef.child(currentUserId).child(receiverId).get().await()
                val isReceived = receivedRequestDbRef.child(receiverId).child(currentUserId).get().await()
                if (isSent.exists() && isReceived.exists()) {
                    sentRequestDbRef.child(currentUserId).child(receiverId).removeValue().await()
                    receivedRequestDbRef.child(receiverId).child(currentUserId).removeValue().await()
                    _cancelRequestStatus.onNext(Notification.createOnNext(true))
                } else {
                    _cancelRequestStatus.onNext(Notification.createOnNext(false))
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error cancel friend request", e)
            _cancelRequestStatus.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
    }

    suspend fun acceptRequest(senderId: String) {
        Log.d(TAG, "acceptRequest")
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val isReceived = receivedRequestDbRef.child(currentUserId).child(senderId).get().await()
                val isSent = sentRequestDbRef.child(senderId).child(currentUserId).get().await()
                if (isReceived.exists() && isSent.exists()) {
                    receivedRequestDbRef.child(currentUserId).child(senderId).removeValue().await()
                    sentRequestDbRef.child(senderId).child(currentUserId).removeValue().await()
                    val snapshot = availableUsersDbRef.get().await()
                    for (childSnapshot in snapshot.children) {
                        val availableUser = childSnapshot.getValue(Friend::class.java)
                        availableUser?.apply {
                            if (id == currentUserId) {
                                val friend = Friend(currentUserId, firstName, lastName, profileImage, email, mobile)
                                friendListDbRef.child(senderId).child(currentUserId).setValue(friend).await()
                            } else if (id == senderId) {
                                val friend = Friend(senderId, firstName, lastName, profileImage, email, mobile)
                                friendListDbRef.child(currentUserId).child(senderId).setValue(friend).await()
                            }
                        }
                    }
                    _acceptRequestStatus.onNext(Notification.createOnNext(true))
                } else {
                    _acceptRequestStatus.onNext(Notification.createOnNext(false))
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error accepting friend request", e)
            _acceptRequestStatus.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
    }

    private val receivedRequestsEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "receivedRequestsEventListener onChildAdded")
            val newRequest = snapshot.getValue(Friend::class.java)
            val lastRequestList = currentMyRequestsList.value
            if (lastRequestList == null) {
                val firstRequest = arrayListOf<Friend>()
                firstRequest.add(newRequest!!)
                _currentMyRequestsList.postValue(firstRequest)
                _myRequestsList.onNext(Notification.createOnNext(firstRequest))
            } else {
                lastRequestList.let {
                    it.add(newRequest!!)
                    _currentMyRequestsList.postValue(it)
                    _myRequestsList.onNext(Notification.createOnNext(it))
                }
            }
            _availableUserListChange.onNext(Notification.createOnNext(true))
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {
            Log.d(TAG, "receivedRequestsEventListener onChildRemoved")
            val requestCancelled = snapshot.getValue(Friend::class.java)
            val lastRequestList = currentMyRequestsList.value
            lastRequestList?.let {
                it.remove(requestCancelled!!)
                _currentMyRequestsList.postValue(it)
                _myRequestsList.onNext(Notification.createOnNext(it))
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }
    private val sentRequestsEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {
            Log.d(TAG, "sentRequestsEventListener onChildRemoved")
            val requestAccepted = snapshot.getValue(Friend::class.java)
            val lastSentList = currentSentRequestsList.value
            lastSentList?.let {
                it.remove(requestAccepted!!)
                _currentSentRequestsList.postValue(it)
                _sentRequestsList.onNext(Notification.createOnNext(it))
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }
    private val friendAddEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "friendAddEventListener onChildAdded")
            val friendAdded = snapshot.getValue(Friend::class.java)
            val lastMyFriendList = currentMyFriendList.value
            if (lastMyFriendList == null) {
                val firstFriend = arrayListOf<Friend>()
                firstFriend.add(friendAdded!!)
                _currentMyFriendList.postValue(firstFriend)
                _myFriendList.onNext(Notification.createOnNext(firstFriend))
            } else {
                lastMyFriendList.let {
                    it.add(friendAdded!!)
                    _currentMyFriendList.postValue(it)
                    _myFriendList.onNext(Notification.createOnNext(it))
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }
    private val availableUserAddEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "availableUserAddEventListener onChildAdded")
            val newUser = snapshot.getValue(Friend::class.java)
            val lastAvailableUserList = currentAvailableUserList.value
            if (lastAvailableUserList == null) {
                val secondUser = arrayListOf<Friend>()
                secondUser.add(newUser!!)
                _currentAvailableUserList.postValue(secondUser)
                _availableUserList.onNext(Notification.createOnNext(secondUser))
            } else {
                lastAvailableUserList.let {
                    it.add(newUser!!)
                    _currentAvailableUserList.postValue(it)
                    _availableUserList.onNext(Notification.createOnNext(it))
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    fun registerEventChangeListeners() {
        Log.d(TAG, "registerEventChangeListeners")
        mAuth.currentUser?.uid?.let { currentUserId ->
            receivedRequestDbRef.child(currentUserId).addChildEventListener(receivedRequestsEventListener)
            sentRequestDbRef.child(currentUserId).addChildEventListener(sentRequestsEventListener)
            friendListDbRef.child(currentUserId).addChildEventListener(friendAddEventListener)
            availableUsersDbRef.addChildEventListener(availableUserAddEventListener)
        }
    }

    fun removeAllEventListeners() {
        Log.d(TAG, "removeAllEventListeners")
        mAuth.currentUser?.uid?.let { currentUserId ->
            receivedRequestDbRef.child(currentUserId).removeEventListener(receivedRequestsEventListener)
            sentRequestDbRef.child(currentUserId).removeEventListener(sentRequestsEventListener)
            friendListDbRef.child(currentUserId).removeEventListener(friendAddEventListener)
            availableUsersDbRef.removeEventListener(availableUserAddEventListener)
        }
    }

    suspend fun getMyRequestsList(): ArrayList<Friend> {
        Log.d(TAG, "getMyRequestsList")
        val requestsList = arrayListOf<Friend>()
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val snapshot = receivedRequestDbRef.child(currentUserId).get().await()
                val genericTypeIndicator = object : GenericTypeIndicator<Map<String, Friend>>() {}
                val receivedRequest = snapshot.getValue(genericTypeIndicator)
                receivedRequest?.let {
                    for ((_, senderInfo) in it) {
                        requestsList.add(senderInfo)
                    }
                }
                _currentMyRequestsList.postValue(requestsList)
                _myRequestsList.onNext(Notification.createOnNext(requestsList))
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error getting requests list", e)
            _myRequestsList.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
        Log.d(TAG, "requestsList $requestsList")
        return requestsList
    }

    suspend fun getAllUsersList(): ArrayList<Friend> {
        Log.d(TAG, "getAllUsersList")
        val userList = arrayListOf<Friend>()
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val snapshot = availableUsersDbRef.get().await()
                for (childSnapshot in snapshot.children) {
                    val availableUser = childSnapshot.getValue(Friend::class.java)
                    availableUser?.let {
                        if (it.id != currentUserId) {
                            val isSent = sentRequestDbRef.child(currentUserId).child(it.id).get().await()
                            val isReceived = receivedRequestDbRef.child(currentUserId).child(it.id).get().await()
                            val isFriend = friendListDbRef.child(currentUserId).child(it.id).get().await()
                            if (!isSent.exists() && !isReceived.exists() && !isFriend.exists()) {
                                userList.add(it)
                            }
                        }
                    }
                }
                _currentAvailableUserList.postValue(userList)
                _availableUserList.onNext(Notification.createOnNext(userList))
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error getting all available user list", e)
            _availableUserList.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
        Log.d(TAG, "userList $userList")
        return userList
    }

    suspend fun getMyFriendsList(): ArrayList<Friend> {
        Log.d(TAG, "getMyFriendsList")
        val myFriendList = arrayListOf<Friend>()
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val snapshot = friendListDbRef.child(currentUserId).get().await()
                val genericTypeIndicator = object : GenericTypeIndicator<Map<String, Friend>>() {}
                val friend = snapshot.getValue(genericTypeIndicator)
                friend?.let {
                    for ((_, friendInfo) in it) {
                        myFriendList.add(friendInfo)
                    }
                }
                _currentMyFriendList.postValue(myFriendList)
                _myFriendList.onNext(Notification.createOnNext(myFriendList))
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error getting my friends list", e)
            _myFriendList.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
        Log.d(TAG, "myFriendList $myFriendList")
        return myFriendList
    }

    suspend fun getSentRequestsList(): ArrayList<Friend> {
        Log.d(TAG, "getSentRequestsList")
        val sentRequestsList = arrayListOf<Friend>()
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val snapshot = sentRequestDbRef.child(currentUserId).get().await()
                val genericTypeIndicator = object : GenericTypeIndicator<Map<String, Friend>>() {}
                val sentRequests = snapshot.getValue(genericTypeIndicator)
                sentRequests?.let {
                    for ((_, receiverInfo) in it) {
                        sentRequestsList.add(receiverInfo)
                    }
                }
                _currentSentRequestsList.postValue(sentRequestsList)
                _sentRequestsList.onNext(Notification.createOnNext(sentRequestsList))
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error getting sent requests list", e)
            _sentRequestsList.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
        Log.d(TAG, "sentRequestsList $sentRequestsList")
        return sentRequestsList
    }

    companion object {
        const val TAG = "FriendsRepository"
    }
}