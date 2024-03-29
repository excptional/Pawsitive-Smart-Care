package com.by.pawsitive.db.repos

import com.by.pawsitive.db.Response
import com.by.pawsitive.db.data_classes.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository {

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun logOut(): Flow<Unit> = flow {
        firebaseAuth.signOut()
        emit(Unit)
    }

    fun login(phone: String, password: String): Flow<Response<User>> = flow {

        try {
            firebaseAuth.signInWithEmailAndPassword("$phone@gmail.com", password).await()
            emit(Response.Success(firebaseAuth.currentUser!!.uid))
        } catch (e: Exception) {
            emit(Response.Failure(getErrorMessage(e)))
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
    ): Flow<Response<User>> = flow {

        try {
            val salt = BCrypt.gensalt(10, SecureRandom())
            val hashedPassword = BCrypt.hashpw(password, salt)

            firebaseAuth.createUserWithEmailAndPassword("$phone@gmail.com", password).await()

            val user = User(
                firebaseAuth.currentUser!!.uid,
                ownerName,
                "https://firebasestorage.googleapis.com/v0/b/pawsitive-smart-care.appspot.com/o/images%2Fuser_profile.png?alt=media&token=870fad16-156a-43e4-9f58-12fb9b5157ad",
                petName,
                "https://firebasestorage.googleapis.com/v0/b/pawsitive-smart-care.appspot.com/o/images%2Fuser_profile.png?alt=media&token=870fad16-156a-43e4-9f58-12fb9b5157ad",petAge,
                petGender,
                species,
                "$phone@gmail.com",
                hashedPassword
            )
            firebaseDB.collection("Users").document(firebaseAuth.currentUser!!.uid).set(user)
            emit(Response.Success(firebaseAuth.currentUser!!.uid))
        } catch (e: Exception) {
            emit(Response.Failure(getErrorMessage(e)))
        }
    }

    private fun getErrorMessage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

    private suspend fun <T> Task<T>.await(): T {
        return suspendCancellableCoroutine { cont ->
            addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(task.result!!)
                } else {
                    cont.resumeWithException(task.exception!!)
                }
            }
        }
    }

}