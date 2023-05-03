package com.yamin.chatchat.repositories

import android.accounts.NetworkErrorException
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yamin.chatchat.data.models.Friend
import com.yamin.chatchat.data.models.User
import io.reactivex.rxjava3.core.Notification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val mAuth = Firebase.auth
    private val userListDbRef = FirebaseDatabase.getInstance().reference.child("users")
    private val userListPublicDbRef = FirebaseDatabase.getInstance().reference.child("users_public")

    private val _isSignUpSuccessful = PublishSubject.create<Notification<Pair<Boolean, String?>>>()
    val isSignUpSuccessful: Observable<Notification<Pair<Boolean, String?>>>
        get() = _isSignUpSuccessful

    private val _isLogInSuccessful = PublishSubject.create<Notification<Pair<Boolean, String?>>>()
    val isLogInSuccessful: Observable<Notification<Pair<Boolean, String?>>>
        get() = _isLogInSuccessful

    /*fun signUpUserInFirebase(
        email: String,
        imageData: ByteArray,
        password: String,
        firstName: String,
        lastName: String,
        mobile: String
    ) {
        Log.d(TAG, "signUpUserInFirebase")
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "User create successful")

                        val userId = it.result?.user?.uid as String
                        val storageRef = FirebaseStorage.getInstance().reference.child("user_images/${userId}")
                        val uploadTask = storageRef.putBytes(imageData)

                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                Log.d(TAG, "Profile image upload task unsuccessful ${task.exception?.message}")
                                notifySignUpStatus(false, task.exception?.message.toString())
                                //throw  task.exception as Throwable
                            }

                            Log.d(TAG, "Profile image upload task successful")
                            storageRef.downloadUrl
                        }.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                Log.d(TAG, "Profile image download url get successful")

                                val profileImageDownloadUrl = task2.result.toString()
                                val user = User(userId, email, profileImageDownloadUrl, firstName, lastName, mobile, password)
                                databaseRef.child(userId).setValue(user).addOnCompleteListener { finalTask ->
                                    if (finalTask.isSuccessful) {
                                        Log.d(TAG, "User data upload task unsuccessful")

                                        notifySignUpStatus(true)
                                    } else {
                                        Log.d(TAG, "User data upload task unsuccessful ${finalTask.exception?.message}")
                                        notifySignUpStatus(false, finalTask.exception?.message.toString())
                                        //throw  task2.exception as Throwable
                                    }
                                }
                            } else {
                                Log.d(TAG, "Profile image download url get unsuccessful ${task2.exception?.message}")
                                notifySignUpStatus(false, task2.exception?.message.toString())
                                //throw  task2.exception as Throwable
                            }
                        }

                    } else {
                        Log.d(TAG, "User create unsuccessful ${it.exception?.message}")
                        notifySignUpStatus(false, it.exception?.message.toString())
                        //throw  it.exception as Throwable
                    }
                }
        } catch (e: Exception) {
            Log.d(TAG, "signUpUserInFirebase unsuccessful ${e.message}")
            e.printStackTrace()
            //notifySignUpStatus(false, e.message.toString())
        }
    }*/

    suspend fun logInUser(emailOrMobile: String, password: String) {
        Log.d(TAG, "logInUser")

        try {
            mAuth.signInWithEmailAndPassword(emailOrMobile, password).await()
            val userId = mAuth.currentUser?.uid ?: throw NetworkErrorException()
            _isLogInSuccessful.onNext(Notification.createOnNext(Pair(true, userId)))
        } catch (e: Exception) {
            Log.d(TAG, "log In failed ${e.message}")
            e.printStackTrace()
            _isLogInSuccessful.onNext(Notification.createOnNext(Pair(false, e.message)))
        }

    }

    fun logOutUserFromApp() {
        Log.d(TAG, "logOutUserFromApp")
        mAuth.signOut()
    }

    suspend fun getCurrentUser(): User? {
        Log.d(TAG, "getCurrentUser")

        return try {
            val currentUserId = mAuth.currentUser?.uid
            val snapshot = currentUserId?.let { userListDbRef.child(it).get().await() }
            snapshot?.getValue(User::class.java)
        } catch (e: Exception) {
            Log.d(TAG, "Error getting current user", e)
            e.printStackTrace()
            null

        }
    }

    fun getCurrentUserId(): String? {
        Log.d(TAG, "getCurrentUserId")
        return mAuth.currentUser?.uid
    }

    suspend fun signUpUserInFirebase(
        email: String,
        imageData: ByteArray,
        password: String,
        firstName: String,
        lastName: String,
        mobile: String
    ) {
        Log.d(TAG, "signUpUserInFirebase")

        try {
            val authResult = mAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid as String
            Log.d(TAG, "userId $userId")

            val storageRef = FirebaseStorage.getInstance().reference.child("user_images/${userId}")
            storageRef.putBytes(imageData).await()

            val getDownloadUriTask = storageRef.downloadUrl.await()
            val profileImageDownloadUrl = getDownloadUriTask.toString()
            Log.d(TAG, "profileImageDownloadUrl $profileImageDownloadUrl")

            val user = User(userId, email, profileImageDownloadUrl, firstName, lastName, mobile, password)
            val friend = Friend(userId, firstName, lastName, profileImageDownloadUrl, email, mobile)
            userListDbRef.child(userId).setValue(user).await()
            userListPublicDbRef.child(userId).setValue(friend).await()
            notifySignUpStatus(true)
        } catch (e: Exception) {
            Log.d(TAG, "Exception ${e.message}")
            e.printStackTrace()
            notifySignUpStatus(false, e.message.toString())
        }

    }

    private fun notifySignUpStatus(status: Boolean, errorMessage: String? = null) {
        Log.d(TAG, "notifySignUpStatus status $status errorMessage $errorMessage")
        try {
            _isSignUpSuccessful.onNext(Notification.createOnNext(Pair(status, errorMessage)))
        } catch (e: Exception) {
            _isSignUpSuccessful.onError(Throwable(e.message))
        }
    }

    companion object {
        const val TAG = "UserRepository"
    }
}