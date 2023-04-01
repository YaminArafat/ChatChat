package com.yamin.chatchat.repositories

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yamin.chatchat.data.models.User
import io.reactivex.rxjava3.core.Notification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {

    private val mAuth = Firebase.auth
    private val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

    private val _isSignUpSuccessful = PublishSubject.create<Notification<Pair<Boolean, String?>>>()
    val isSignUpSuccessful: Observable<Notification<Pair<Boolean, String?>>>
        get() = _isSignUpSuccessful

    /*fun signUpUserInFirebase(
        email: String,
        imageData: ByteArray,
        password: String,
        firstName: String,
        lastName: String,
        mobile: String
    ) {
        Log.d(TAG, "signUpUserInFirebase")

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "User create successful")

                    val userId = it.result?.user?.uid as String
                    val storageRef = FirebaseStorage.getInstance().reference.child("user_images/${userId}")
                    val uploadTask = storageRef.putBytes(imageData)

                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            Log.d(TAG, "Profile image upload task unsuccessful")
                            notifySignUpStatus(false, task.exception?.message.toString())
                            throw  task.exception as FirebaseNetworkException
                        }

                        Log.d(TAG, "Profile image upload task successful")
                        storageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Profile image download url get successful")

                            val profileImageDownloadUrl = task.result.toString()
                            val user = User(userId, email, profileImageDownloadUrl, firstName, lastName, mobile, password)
                            databaseRef.child(userId).setValue(user)
                            notifySignUpStatus(true)
                        } else {
                            Log.d(TAG, "Profile image download url get unsuccessful")
                            notifySignUpStatus(false, task.exception?.message.toString())
                            throw  task.exception as FirebaseNetworkException                        }
                    }

                } else {
                    Log.d(TAG, "User create unsuccessful")
                    notifySignUpStatus(false, it.exception?.message.toString())
                    throw  it.exception as FirebaseException
                }
            }
    }*/

    suspend fun logInUserToApp(emailOrMobile: String, password: String) {
        Log.d(TAG, "logInUserToApp")

        mAuth.signInWithEmailAndPassword(emailOrMobile, password).await()
    }

    fun logOutUserFromApp() {
        Log.d(TAG, "logOutUserFromApp")
        mAuth.signOut()
    }

    suspend fun getCurrentUser(): User? {
        Log.d(TAG, "getCurrentUser")

        return withContext(Dispatchers.IO) {
            try {
                val currentUserId = mAuth.currentUser?.uid
                val snapshot = currentUserId?.let { databaseRef.child(it).get().await() }
                snapshot?.getValue(User::class.java)
            } catch (e: Exception) {
                Log.d(TAG, "Error getting current user", e)
                null
            }
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

            databaseRef.child(userId).setValue(user).await()
            notifySignUpStatus(true)
        } catch (e: FirebaseNetworkException) {
            Log.d(TAG, "Exception ${e.message}")
            notifySignUpStatus(false, e.message.toString())
        }

    }

    private fun notifySignUpStatus(status: Boolean, errorMessage: String? = null) {
        Log.d(TAG, "notifySignUpStatus status $status errorMessage $errorMessage")

        if (status) {
            _isSignUpSuccessful.onNext(Notification.createOnNext(Pair(status, null)))
            _isSignUpSuccessful.onComplete()
        } else
            _isSignUpSuccessful.onError(Exception(errorMessage))
    }

    companion object {
        const val TAG = "UserRepository"
    }
}