package com.by.pawsitive.db.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.by.pawsitive.db.repos.AppRepository

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val appRepository = AppRepository(application)


}