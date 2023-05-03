package com.yamin.chatchat.repositories

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.yamin.chatchat.data.models.Friend
import io.reactivex.rxjava3.core.Notification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.tasks.await

class FriendsRepository {

    private val mAuth = Firebase.auth
    private val availableUsersDbRef = FirebaseDatabase.getInstance().getReference("users_public")
    private val friendListDbRef = FirebaseDatabase.getInstance().reference.child("friends")
    private val friendRequestDbRef = FirebaseDatabase.getInstance().reference.child("friend_requests")

    private val _myFriendList = PublishSubject.create<Notification<ArrayList<Friend>>>()
    val myFriendList: Observable<Notification<ArrayList<Friend>>>
        get() = _myFriendList

    private val _availableUserList = PublishSubject.create<Notification<ArrayList<Friend>>>()
    val availableUserList: Observable<Notification<ArrayList<Friend>>>
        get() = _availableUserList

    private val _myRequestsList = PublishSubject.create<Notification<ArrayList<Friend>>>()
    val myRequestsList: Observable<Notification<ArrayList<Friend>>>
        get() = _myRequestsList

    private val _sentRequestsList = PublishSubject.create<Notification<ArrayList<Friend>>>()
    val sentRequestsList: Observable<Notification<ArrayList<Friend>>>
        get() = _sentRequestsList

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
            val currentUserId = mAuth.currentUser?.uid
            val requestKey = "${currentUserId}_$receiverId"
            val isRequested = friendRequestDbRef.child(requestKey).get().await()
            if (!isRequested.exists()) {
                friendRequestDbRef.child(requestKey).setValue(true).await()
                _sendRequestStatus.onNext(Notification.createOnNext(true))
            } else {
                _sendRequestStatus.onNext(Notification.createOnNext(false))
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
            val currentUserId = mAuth.currentUser?.uid
            val requestKey = "${currentUserId}_$receiverId"
            val isRequested = friendRequestDbRef.child(requestKey).get().await()
            if (isRequested.exists() && isRequested.value == true) {
                friendRequestDbRef.child(requestKey).removeValue().await()
                _cancelRequestStatus.onNext(Notification.createOnNext(true))
            } else {
                _cancelRequestStatus.onNext(Notification.createOnNext(false))
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
            val currentUserId = mAuth.currentUser?.uid
            val requestKey = "${senderId}_$currentUserId"
            val isRequested = friendRequestDbRef.child(requestKey).get().await()
            if (isRequested.exists() && isRequested.value == true) {
                friendRequestDbRef.child(requestKey).setValue(false).await()
                val snapshot = availableUsersDbRef.get().await()
                for (childSnapshot in snapshot.children) {
                    val availableUser = childSnapshot.getValue(Friend::class.java)
                    availableUser?.apply {
                        if (id == currentUserId) {
                            val friend = Friend(currentUserId, firstName, lastName, profileImage, email, mobile)
                            friendListDbRef.child(senderId).setValue(friend).await()
                        } else if (id == senderId && currentUserId != null) {
                            val friend = Friend(senderId, firstName, lastName, profileImage, email, mobile)
                            friendListDbRef.child(currentUserId).setValue(friend).await()
                        }
                    }
                }
                _acceptRequestStatus.onNext(Notification.createOnNext(true))
            } else {
                _acceptRequestStatus.onNext(Notification.createOnNext(false))
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error accepting friend request", e)
            _acceptRequestStatus.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
    }

    suspend fun getMyRequestsList(): ArrayList<Friend> {
        Log.d(TAG, "getMyRequestsList")
        val requestsList = arrayListOf<Friend>()
        try {
            val currentUserId = mAuth.currentUser?.uid
            val snapshot = availableUsersDbRef.get().await()
            for (childSnapshot in snapshot.children) {
                val availableUser = childSnapshot.getValue(Friend::class.java)
                availableUser?.let {
                    val requestKey = "${it.id}_${currentUserId}"
                    val isFriendOrRequested = friendRequestDbRef.child(requestKey).get().await()
                    if (isFriendOrRequested.exists() && isFriendOrRequested.value == true) {
                        requestsList.add(it)
                    }
                }
            }
            _myRequestsList.onNext(Notification.createOnNext(requestsList))
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
            val currentUserId = mAuth.currentUser?.uid
            val snapshot = availableUsersDbRef.get().await()
            for (childSnapshot in snapshot.children) {
                val availableUser = childSnapshot.getValue(Friend::class.java)
                availableUser?.let {
                    val requestKey1 = "${currentUserId}_${it.id}"
                    val requestKey2 = "${it.id}_$currentUserId"
                    val isFriendOrRequested1 = friendRequestDbRef.child(requestKey1).get().await()
                    val isFriendOrRequested2 = friendRequestDbRef.child(requestKey2).get().await()
                    if (!isFriendOrRequested1.exists() && !isFriendOrRequested2.exists()) {
                        userList.add(it)
                    }
                }
            }
            _availableUserList.onNext(Notification.createOnNext(userList))
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
            val currentUserId = mAuth.currentUser?.uid
            currentUserId?.let { userId ->
                val snapshot = friendListDbRef.child(userId).get().await()
                for (childSnapshot in snapshot.children) {
                    val friend = childSnapshot.getValue(Friend::class.java)
                    friend?.let {
                        myFriendList.add(it)
                    }
                }
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
            val currentUserId = mAuth.currentUser?.uid
            val snapshot = availableUsersDbRef.get().await()
            for (childSnapshot in snapshot.children) {
                val availableUser = childSnapshot.getValue(Friend::class.java)
                availableUser?.let {
                    val requestKey = "${currentUserId}_${it.id}"
                    val isRequested = friendRequestDbRef.child(requestKey).get().await()
                    if (isRequested.exists() && isRequested.value == true) {
                        sentRequestsList.add(it)
                    }
                }
            }
            _sentRequestsList.onNext(Notification.createOnNext(sentRequestsList))
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