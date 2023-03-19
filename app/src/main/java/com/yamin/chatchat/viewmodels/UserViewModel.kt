package com.yamin.chatchat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yamin.chatchat.data.models.User
import com.yamin.chatchat.repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private var imageFilePath: String? = null

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>
        get() = _currentUser

    private val _currentUserId = MutableLiveData<String>()
    val currentUserId: LiveData<String>
        get() = _currentUserId


    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignedIn: LiveData<Boolean>
        get() = _isSignedIn

    fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        mobile: String
    ) {
        Log.d(TAG, "signUp")

        viewModelScope.launch {
            try {
                imageFilePath?.let {
                    userRepository.signUpUserInFirebase(
                        email,
                        it,
                        password,
                        firstName,
                        lastName,
                        mobile
                    )
                    //_isSignedIn.value = true
                }
            } catch (e: Exception) {
                Log.d(TAG, "Sign up unsuccessful", e)
                //_isSignedIn.value = false
            }
        }
    }

    fun logIn(emailOrMobile: String, password: String) {
        Log.d(TAG, "logIn")

        viewModelScope.launch {
            try {
                userRepository.logInUserToApp(emailOrMobile, password)
                _isSignedIn.value = true
            } catch (e: Exception) {
                Log.d(TAG, "Log in unsuccessful", e)
                _isSignedIn.value = false
            }
        }
    }

    fun logOut() {
        Log.d(TAG, "logOut")

        userRepository.logOutUserFromApp()
        _isSignedIn.value = false
    }

    fun getCurrentUserData(): User? {
        Log.d(TAG, "getCurrentUserData")

        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
            } catch (e: Exception) {
                Log.d(TAG, "Error getting current user", e)
            }
        }
        return _currentUser.value
    }

    fun getCurrentUserId(): String? {
        Log.d(TAG, "getCurrentUserId")

        _currentUserId.value = userRepository.getCurrentUserId()
        return _currentUserId.value
    }

    fun getImageFilePath(): String? {
        return imageFilePath
    }

    fun setImageFilePath(path: String?) {
        Log.d(TAG, "setImageFilePath")

        imageFilePath = path
    }

    companion object {
        const val TAG = "UserViewModel"
    }
}