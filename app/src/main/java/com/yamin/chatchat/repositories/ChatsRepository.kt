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
import com.yamin.chatchat.data.models.Chats
import com.yamin.chatchat.data.models.Friend
import com.yamin.chatchat.data.models.Message
import com.yamin.chatchat.data.models.Participants
import io.reactivex.rxjava3.core.Notification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.tasks.await

class ChatsRepository {
    private val mAuth = Firebase.auth
    private val chatsDbRef = FirebaseDatabase.getInstance().getReference("chats")
    private val chatsListDbRef = FirebaseDatabase.getInstance().getReference("chats_list")
    private val chatsKeysDbRef = FirebaseDatabase.getInstance().getReference("chats_keys")
    private val availableUsersDbRef = FirebaseDatabase.getInstance().getReference("users_public")

    private var messageCount = 0

    private val _chatsList = PublishSubject.create<Notification<ArrayList<Chats>>>()
    val chatsList: Observable<Notification<ArrayList<Chats>>>
        get() = _chatsList

    private val _currentChatsList = MutableLiveData<ArrayList<Chats>>()
    val currentChatsList: LiveData<ArrayList<Chats>>
        get() = _currentChatsList

    private val _liveMessage = PublishSubject.create<Notification<Message>>()
    val liveMessage: Observable<Notification<Message>>
        get() = _liveMessage

    suspend fun sendMessage(message: String, receiverId: String, timestamp: Long) {
        Log.d(TAG, "sendMessage")
        try {
            mAuth.currentUser?.uid?.let { senderId ->
                val chatKeySnapshot = chatsKeysDbRef.child(senderId).child(receiverId).get().await()
                val chatKey: String?
                if (!chatKeySnapshot.exists()) {
                    chatKey = chatsDbRef.push().key
                    chatKey?.let { key ->
                        chatsDbRef.child(key).child("participants").setValue(Participants(senderId, receiverId)).await()
                        chatsKeysDbRef.child(senderId).child(receiverId).setValue(key).await()
                        chatsKeysDbRef.child(receiverId).child(senderId).setValue(key).await()
                    }
                } else {
                    chatKey = chatKeySnapshot.getValue(String::class.java)
                }
                chatKey?.let { key ->
                    val newMessage = Message(senderId, message, receiverId, timestamp)
                    chatsDbRef.child(key).child("messages").push().setValue(newMessage).await()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error sending message", e)
            e.printStackTrace()
        }
    }

    suspend fun observeLiveMessages(friendId: String) {
        Log.d(TAG, "observeLiveMessages")
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val chatKeySnapshot = chatsKeysDbRef.child(currentUserId).child(friendId).get().await()
                if (chatKeySnapshot.exists()) {
                    val chatKey = chatKeySnapshot.getValue(String::class.java)
                    val conversationDbRef = chatsDbRef.child(chatKey!!).child("messages")
                    val currentUserSnapshot = availableUsersDbRef.child(currentUserId).get().await()
                    val friendSnapshot = availableUsersDbRef.child((friendId)).get().await()
                    val currentUserPublicData = currentUserSnapshot.getValue(Friend::class.java)
                    val friendPublicData = friendSnapshot.getValue(Friend::class.java)
                    conversationDbRef.addChildEventListener(object : ChildEventListener {
                        var childCount = 0
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            Log.d(TAG, "conversationDbRef onChildAdded")
                            childCount++
                            if (childCount > messageCount) {
                                val newMessage = snapshot.getValue(Message::class.java)
                                newMessage?.let {
                                    Log.d(TAG, "_liveMessage onNext")
                                    _liveMessage.onNext(Notification.createOnNext(it))
                                    friendPublicData?.apply {
                                        val chatSummaryCurrentUser = Chats(id, firstName, lastName, profileImage, it)
                                        chatsListDbRef.child(currentUserId).child(chatKey).setValue(chatSummaryCurrentUser)
                                    }
                                    currentUserPublicData?.apply {
                                        val chatSummaryFriend = Chats(id, firstName, lastName, profileImage, it)
                                        chatsListDbRef.child(friendId).child(chatKey).setValue(chatSummaryFriend)
                                    }
                                }
                            }
                        }
                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onChildRemoved(snapshot: DataSnapshot) {}
                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onCancelled(error: DatabaseError) {}

                    })
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error observeLiveMessages", e)
            _liveMessage.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
    }

    private val liveChatsListListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "liveChatsListListener onChildAdded")
            val newChatSummary = snapshot.getValue(Chats::class.java)
            val lastChatSummaryList = currentChatsList.value
            if (lastChatSummaryList == null) {
                val firstChat = arrayListOf<Chats>()
                firstChat.add(newChatSummary!!)
                _chatsList.onNext(Notification.createOnNext(firstChat))
                _currentChatsList.postValue(firstChat)
            } else {
                lastChatSummaryList.let {
                    it.add(newChatSummary!!)
                    _chatsList.onNext(Notification.createOnNext(it))
                    _currentChatsList.postValue(it)
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "liveChatsListListener onChildChanged")
            val updatedChatSummary = snapshot.getValue(Chats::class.java)
            val lastChatSummaryList = currentChatsList.value
            lastChatSummaryList?.let { lastChatList ->
                val previousChatSummaryIndex = lastChatList.indexOfFirst { it.userId == updatedChatSummary?.userId }
                if (previousChatSummaryIndex != -1) {
                    lastChatList[previousChatSummaryIndex] = updatedChatSummary!!
                    _chatsList.onNext(Notification.createOnNext(lastChatList))
                    _currentChatsList.postValue(lastChatList)
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(snapshot: DatabaseError) {}
    }

    fun observeLiveChatsList() {
        Log.d(TAG, "observeLiveChatsList")
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                chatsListDbRef.child(currentUserId).addChildEventListener(liveChatsListListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeAllEventListeners() {
        mAuth.currentUser?.uid?.let { currentUserId ->
            chatsListDbRef.child(currentUserId).removeEventListener(liveChatsListListener)
        }
    }

    suspend fun getFullConversation(friendId: String): ArrayList<Message> {
        Log.d(TAG, "getFullConversation")
        val fullConversation = arrayListOf<Message>()
        messageCount = 0
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val chatKeySnapshot = chatsKeysDbRef.child(currentUserId).child(friendId).get().await()
                if (chatKeySnapshot.exists()) {
                    val chatKey = chatKeySnapshot.getValue(String::class.java)
                    val conversationDbRef = chatsDbRef.child(chatKey!!).child("messages")
                    val sortConversationQuery = conversationDbRef.orderByChild("messageTimestamp")
                    val conversationSnapshot = sortConversationQuery.get().await()
                    for (childSnapshot in conversationSnapshot.children) {
                        val message = childSnapshot.getValue(Message::class.java)
                        message?.let {
                            fullConversation.add(it)
                            messageCount++
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error getting full conversation", e)
            e.printStackTrace()
        }
        Log.d(TAG, "fullConversation $fullConversation")
        return fullConversation
    }

    suspend fun getChatsList(): ArrayList<Chats> {
        Log.d(TAG, "getChatsList")
        val chatList = arrayListOf<Chats>()
        try {
            mAuth.currentUser?.uid?.let { currentUserId ->
                val currentUserChatsListDbRef = chatsListDbRef.child(currentUserId)
                val sortChatsListQuery = currentUserChatsListDbRef.orderByChild("lastMessage/messageTimestamp")
                val snapshot = sortChatsListQuery.get().await()
                val genericTypeIndicator = object : GenericTypeIndicator<Map<String, Chats>>() {}
                val chat = snapshot.getValue(genericTypeIndicator)
                chat?.let {
                    for ((_, chatInfo) in it) {
                        chatList.add(chatInfo)
                    }
                }
                _chatsList.onNext(Notification.createOnNext(chatList))
                _currentChatsList.postValue(chatList)
            }

        } catch (e: Exception) {
            Log.d(TAG, "Error getting chats list", e)
            _chatsList.onNext(Notification.createOnError(e))
            e.printStackTrace()
        }
        Log.d(TAG, "chatList $chatList")
        return chatList
    }

    fun getCurrentUserId(): String? {
        Log.d(TAG, "getCurrentUserId")
        return mAuth.currentUser?.uid
    }

    companion object {
        const val TAG = "ChatsRepository"
    }
}