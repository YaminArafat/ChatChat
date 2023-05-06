package com.yamin.chatchat.repositories

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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

    private val _chatsList = PublishSubject.create<Notification<ArrayList<Chats>>>()
    val chatsList: Observable<Notification<ArrayList<Chats>>>
        get() = _chatsList

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
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val newMessage = snapshot.getValue(Message::class.java)
                            newMessage?.let {
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

    suspend fun getFullConversation(friendId: String): ArrayList<Message> {
        Log.d(TAG, "getFullConversation")
        val fullConversation = arrayListOf<Message>()
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
                        message?.let { fullConversation.add(it) }
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
                for (childSnapshot in snapshot.children) {
                    val chat = childSnapshot.getValue(Chats::class.java)
                    chat?.let {
                        chatList.add(it)
                    }
                }
                _chatsList.onNext(Notification.createOnNext(chatList))
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