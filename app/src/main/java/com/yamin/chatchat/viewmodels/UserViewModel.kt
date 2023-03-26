package com.yamin.chatchat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yamin.chatchat.data.models.User
import com.yamin.chatchat.repositories.UserRepository
import com.yamin.chatchat.utils.SuccessCallback
import com.yamin.chatchat.utils.Type
import kotlinx.coroutines.launch

class UserViewModel : ViewModel(), SuccessCallback {

    private val userRepository = UserRepository()

    private var imageData: Any? = null

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>
        get() = _currentUser

    private val _currentUserId = MutableLiveData<String>()
    val currentUserId: LiveData<String>
        get() = _currentUserId


    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignedIn: LiveData<Boolean>
        get() = _isSignedIn

    private val _signUpSuccessful = MutableLiveData<Boolean>()
    val signUpSuccessful: LiveData<Boolean>
        get() = _signUpSuccessful

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

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
                imageData?.let {
                    userRepository.signUpUserInFirebase(
                        email,
                        it,
                        password,
                        firstName,
                        lastName,
                        mobile
                    )
                }
                //_isSignedIn.value = true
                onSuccess(Type.SIGN_UP)
            } catch (e: Exception) {
                Log.d(TAG, "Sign up unsuccessful ${e.message}")
                //_isSignedIn.value = false
                onFailure(Type.SIGN_UP, e.message.toString())
            }
        }
    }

    fun getSignUpStatus() {
        signUpSuccessful.observeForever { success ->
            _signUpSuccessful.postValue(success)
        }
    }

    fun isSuccessfulSignUp(): Boolean? {
        Log.d(TAG, "isSuccessfulSignUp ${signUpSuccessful.value}")
        return signUpSuccessful.value
    }

    fun getErrorMessage(): String? {
        Log.d(TAG, "getErrorMessage")
        return errorMessage.value
    }

    fun logIn(emailOrMobile: String, password: String) {
        Log.d(TAG, "logIn")

        viewModelScope.launch {
            try {
                userRepository.logInUserToApp(emailOrMobile, password)
                _isSignedIn.postValue(true)
            } catch (e: Exception) {
                Log.d(TAG, "Log in unsuccessful", e)
                _isSignedIn.postValue(false)
            }
        }
    }

    fun logOut() {
        Log.d(TAG, "logOut")

        userRepository.logOutUserFromApp()
        _isSignedIn.postValue(false)
    }

    fun getCurrentUserData(): User? {
        Log.d(TAG, "getCurrentUserData")

        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                _currentUser.postValue(user)
            } catch (e: Exception) {
                Log.d(TAG, "Error getting current user", e)
            }
        }
        return currentUser.value
    }

    fun getCurrentUserId(): String? {
        Log.d(TAG, "getCurrentUserId")

        _currentUserId.postValue(userRepository.getCurrentUserId())
        return currentUserId.value
    }

    fun setImageData(data: Any?) {
        Log.d(TAG, "setImageData")

        imageData = data
    }

    override fun onSuccess(operationType: Type) {
        Log.d(TAG, "onSuccess")

        if (operationType == Type.SIGN_UP) {
            _signUpSuccessful.postValue(true)
            Log.d(TAG, "signUpSuccessful ${signUpSuccessful.value} _signUpSuccessful ${_signUpSuccessful.value}")
        }
    }

    override fun onFailure(operationType: Type, message: String) {
        Log.d(TAG, "onFailure")

        if (operationType == Type.SIGN_UP) {
            _signUpSuccessful.postValue(false)
            _errorMessage.postValue(message)
        }
    }

    companion object {
        const val TAG = "UserViewModel"
    }
}