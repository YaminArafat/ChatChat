package com.yamin.chatchat.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yamin.chatchat.data.models.User
import com.yamin.chatchat.repositories.UserRepository
import com.yamin.chatchat.utils.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private var disposable = CompositeDisposable()

    private var imageData: ByteArray? = null

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>
        get() = _currentUser

    private val _currentUserId = MutableLiveData<String>()
    val currentUserId: LiveData<String>
        get() = _currentUserId


    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignedIn: LiveData<Boolean>
        get() = _isSignedIn

    private val _signUpSuccessful = MutableLiveData<Response<Pair<Boolean, String?>>>()
    val signUpSuccessful: LiveData<Response<Pair<Boolean, String?>>>
        get() = _signUpSuccessful

    private val _logInSuccessful = MutableLiveData<Response<Pair<Boolean, String?>>>()
    val logInSuccessful: LiveData<Response<Pair<Boolean, String?>>>
        get() = _logInSuccessful

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    init {
        Log.d(TAG, "init")
        observeSignUpStatus()
    }

    private fun observeSignUpStatus() {
        Log.d(TAG, "observeSignUpStatus")

        disposable.add(userRepository.isSignUpSuccessful
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnNext) {
                    Log.d(TAG, "_signUpSuccessful isOnNext")
                    it.value?.let { status ->
                        if (status.first) {
                            Log.d(TAG, "isOnNext success")
                            _signUpSuccessful.postValue(Response.Success(status))
                        } else {
                            Log.d(TAG, "isOnNext error")
                            _signUpSuccessful.postValue(Response.Error(status.second!!))
                        }
                    }
                } else if (it.isOnError) {
                    Log.d(TAG, "_signUpSuccessful isOnError")
                    _signUpSuccessful.postValue(Response.Error(it.value?.second!!))
                }
            })
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared")
        super.onCleared()
        disposable.dispose()
    }

    fun resetLogInStatus() {
        Log.d(TAG, "resetLogInStatus")
        _logInSuccessful.postValue(Response.Idle)
    }

    fun resetSignUpStatus() {
        Log.d(TAG, "resetSignUpStatus")
        _signUpSuccessful.postValue(Response.Idle)
    }

    fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        mobile: String
    ) {
        Log.d(TAG, "signUp")

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                    // onSuccess(Type.SIGN_UP)
                } catch (e: Exception) {
                    Log.d(TAG, "Sign up unsuccessful ${e.message}")
                    e.printStackTrace()
                    //_isSignedIn.value = false
                    // onFailure(Type.SIGN_UP, e.message.toString())
                }
            }
        }
    }

    fun getErrorMessage(): String? {
        Log.d(TAG, "getErrorMessage")
        return errorMessage.value
    }

    private fun observeLogInStatus() {
        Log.d(TAG, "observeLogInStatus")

        disposable.add(userRepository.isLogInSuccessful
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isOnNext) {
                    Log.d(TAG, "_logInSuccessful isOnNext")
                    it.value?.let { status ->
                        if (status.first) {
                            Log.d(TAG, "isOnNext success")
                            _logInSuccessful.postValue(Response.Success(status))
                        } else {
                            Log.d(TAG, "isOnNext error")
                            _logInSuccessful.postValue(Response.Error(status.second!!))
                        }
                    }
                } else if (it.isOnError) {
                    Log.d(TAG, "_logInSuccessful isOnError")
                    _logInSuccessful.postValue(Response.Error(it.value?.second!!))
                }
            })
    }

    fun logIn(emailOrMobile: String, password: String) {
        Log.d(TAG, "logIn")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    userRepository.logInUser(emailOrMobile, password)
                    _isSignedIn.postValue(true)
                } catch (e: Exception) {
                    Log.d(TAG, "Log in unsuccessful", e)
                    _isSignedIn.postValue(false)
                }
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
                user?.let {
                    _currentUser.postValue(it)
                }
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

    fun setImageData(data: ByteArray) {
        Log.d(TAG, "setImageData")

        imageData = data
    }

    /*override fun onSuccess(operationType: Type) {
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
    }*/

    companion object {
        const val TAG = "UserViewModel"
    }
}