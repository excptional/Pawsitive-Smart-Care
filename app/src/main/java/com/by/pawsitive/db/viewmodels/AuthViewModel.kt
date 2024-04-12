package com.by.pawsitive.db.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.by.pawsitive.db.Response
import com.by.pawsitive.db.data_classes.User
import com.by.pawsitive.db.repos.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()

    private val _signInResponse = MutableStateFlow<Response<User>>(Response.Loading)

    val signInResponse: MutableStateFlow<Response<User>> get() = _signInResponse

    private val _registerResponse = MutableStateFlow<Response<User>>(Response.Loading)
    val registerResponse: StateFlow<Response<User>> get() = _registerResponse

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    private val _currentUserUid = MutableStateFlow<String?>(null)
    val currentUserUid: StateFlow<String?> get() = _currentUserUid


    fun signIn(phone: String, password: String) {
        viewModelScope.launch {
            authRepository.login(phone, password)
                .collect { response -> _signInResponse.value = response }
        }
    }

    fun register(
        phone: String,
        password: String,
        ownerName: String,
        petName: String,
        petAge: String,
        petGender: String,
        species: String
    ) {
        viewModelScope.launch {
            authRepository.register(phone, password, ownerName, petName, petAge, petGender, species)
                .collect { response -> _registerResponse.value = response }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logOut()
        }
    }

    fun checkLoggedInStatus() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }

    fun fetchCurrentUserUid() {
        _currentUserUid.value = authRepository.getCurrentUserUid()
    }

}