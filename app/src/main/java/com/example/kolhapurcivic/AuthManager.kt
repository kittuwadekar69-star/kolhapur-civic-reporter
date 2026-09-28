package com.example.kolhapurcivic

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthManager {

    private val auth = FirebaseAuth.getInstance()

    fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val userId = result.user?.uid

                val userData = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "role" to role
                )

                db.collection("users")
                    .document(userId!!)
                    .set(userData)

                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    fun getCurrentUser() = auth.currentUser
}