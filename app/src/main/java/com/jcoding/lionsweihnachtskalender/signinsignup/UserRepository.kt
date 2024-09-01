/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetsurvey.signinsignup

import android.util.Log
import androidx.compose.runtime.Immutable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class User {
    @Immutable
    data class LoggedInUser(val email: String, val uui: String? = null, var displayName: String? = null, var role: String? = null) : User() {
        constructor() : this("", null, null, null)
    }

    object GuestUser : User()
    object NoUserLoggedIn : User()
}

/**
 * Repository that holds the logged in user.
 *
 * In a production app, this class would also handle the communication with the backend for
 * sign in and sign up.
 */
object UserRepository {

    private lateinit var auth: FirebaseAuth

    private var _user: User = User.NoUserLoggedIn
    val user: User
        get() = _user

    val database = FirebaseDatabase.getInstance()
    val dbUserRef = database.getReference("users") // Replace "users" with your actual reference path

    fun signIn(email: String, password: String) {

        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener(authListener)

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        if (auth.currentUser!= null)
                            dbUserRef.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        val userData = dataSnapshot.getValue(User.LoggedInUser::class.java) // Replace UserData with your data class
                                        val loggedInUser = _user as User.LoggedInUser
                                        loggedInUser.displayName = userData?.displayName
                                        loggedInUser.role = userData?.role
                                        // FIXME update UI according the role received
                                        Log.d("UserRepository", "loggedInUser: $_user")
                                    } else {
                                        Log.d("UserRepository", "User data not found in the database for UID: ${auth.currentUser!!.uid}")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle errors
                                    println("Failed to read data: ${error.message}")
                                }
                            })
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Log.e("Error beim Login", e.message.toString())
                        //Toast.makeText(this@UserRepository, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


        _user = User.LoggedInUser(email)
    }

    fun signUp(email: String, password: String) {
        auth = FirebaseAuth.getInstance()


        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        // FIXME
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Log.e("Error beim Login", e.message.toString())
                        //Toast.makeText(this@UserRepository, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


        _user = User.LoggedInUser(email)
    }


    val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is signed in
            // You can access user information here (e.g., user.uid, user.email)
            val email = currentUser!!.email.toString()
            val uuid = currentUser!!.uid
            val displayName = currentUser!!.displayName
            val role = "" // by default we do want have a role set!

            _user = User.LoggedInUser(email,uuid,displayName,role)
            Log.d("Auth changed", "User is signed in")
        } else {
            // User is signed out
            // FIXME
            // Redirect to login screen or handle the sign-out state
            Log.d("Auth changed", "User is signed out")
            _user = User.NoUserLoggedIn
            auth.signOut()
        }
    }

    fun signInAsGuest() {
        _user = User.GuestUser
    }

    fun isKnownUserEmail(email: String): Boolean {
        // if the email contains "sign up" we consider it unknown
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        return firebaseUser != null;

    }

    fun signOut() {
        auth.signOut()
        _user = User.NoUserLoggedIn
    }
}

