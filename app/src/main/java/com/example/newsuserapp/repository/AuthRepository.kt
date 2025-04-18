package com.example.newsuserapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.newsuserapp.data.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun signInWithGoogle(idToken: String, callback: (User?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    callback(User(user?.displayName, user?.email, user?.photoUrl.toString()))
                } else {
                    callback(null)
                }
            }
    }
}
