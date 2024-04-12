package com.by.pawsitive.db.repos

import android.app.Application
import com.by.pawsitive.db.data_classes.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AppRepository(private val application: Application) {

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun fetchUserInfo(uid: String): Flow<User> = flow {
        val doc = firebaseDB.collection("Users").document(uid).get().await()
        val userInfo = doc.toObject(User::class.java)
        if (userInfo != null) {
            emit(userInfo)
        }
    }

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

}