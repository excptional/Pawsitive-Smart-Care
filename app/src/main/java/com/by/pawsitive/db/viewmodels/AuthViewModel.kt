package com.by.pawsitive.db.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.by.pawsitive.db.repos.AuthRepository

class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)
}