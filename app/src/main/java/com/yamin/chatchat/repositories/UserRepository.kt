package com.yamin.chatchat.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yamin.chatchat.data.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class UserRepository {

    private val mAuth = Firebase.auth
    private val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

    /*fun signUpUserInFirebase(
        email: String,
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
                    val imageFile = File(imageFilePath)
                    val storageRef = FirebaseStorage.getInstance().reference.child("user_images/${userId}")
                    val uploadTask = storageRef.putFile(Uri.fromFile(imageFile))

                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            Log.d(TAG, "Profile image upload task unsuccessful")
                            Toast.makeText(mContext, task.exception?.message, Toast.LENGTH_SHORT).show()
                            //throw  task.exception as FirebaseNetworkException
                        }

                        Log.d(TAG, "Profile image upload task successful")
                        storageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Profile image download url get successful")

                            val profileImageDownloadUrl = task.result.toString()
                            val user = User(userId, email, profileImageDownloadUrl, firstName, lastName, mobile, password)
                            databaseRef.child(userId).setValue(user)
                        } else {
                            Log.d(TAG, "Profile image download url get unsuccessful")
                            Toast.makeText(mContext, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Log.d(TAG, "User create unsuccessful")
                    Toast.makeText(mContext, it.exception?.message, Toast.LENGTH_SHORT).show()
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
                val currentUserId = mAuth.currentUser?.uid as String
                val snapshot = databaseRef.child(currentUserId).get().await()
                snapshot.getValue(User::class.java)
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
        imageFilePath: String,
        password: String,
        firstName: String,
        lastName: String,
        mobile: String
    ) {
        Log.d(TAG, "signUpUserInFirebase")

        val authResult = mAuth.createUserWithEmailAndPassword(email, password).await()
        val userId = authResult.user?.uid as String

        val imageFile = File(imageFilePath)
        val storageRef = FirebaseStorage.getInstance().reference.child("user_images/${userId}")
        storageRef.putFile(Uri.fromFile(imageFile)).await()

        val getDownloadUrlTask = storageRef.downloadUrl.await()
        val profileImageDownloadUrl = getDownloadUrlTask.toString()

        val user = User(userId, email, profileImageDownloadUrl, firstName, lastName, mobile, password)

        databaseRef.child(userId).setValue(user).await()
    }


    companion object {
        const val TAG = "UserRepository"
    }
}