package com.by.pawsitive.db.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.by.pawsitive.db.data_classes.User
import com.by.pawsitive.db.repos.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val appRepository = AppRepository(application)

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> get() = _userInfo

    suspend fun fetchUserInfo(uid: String) {
        appRepository.fetchUserInfo(uid).collect { userInfo ->
            userInfo.also { _userInfo.value = it }
        }
    }


}