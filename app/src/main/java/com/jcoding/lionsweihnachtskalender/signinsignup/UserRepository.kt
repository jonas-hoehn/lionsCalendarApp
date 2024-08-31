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
import android.widget.Toast
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.contracts.contract

sealed class User {
    @Immutable
    data class LoggedInUser(val email: String) : User()
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

    fun signIn(email: String, password: String) {

        auth = FirebaseAuth.getInstance()

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
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
                        checkLoggedInState()
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

    private fun checkLoggedInState() {
        if(auth.currentUser == null){
            _user = User.NoUserLoggedIn
        } else {
            _user = User.LoggedInUser(auth.currentUser!!.email.toString())
        }
    }

    fun signInAsGuest() {
        _user = User.GuestUser
    }

    fun isKnownUserEmail(email: String): Boolean {
        // if the email contains "sign up" we consider it unknown
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            return !email.contains(email)
        }

        return !email.contains("signup")

    }

    fun signOut() {
        auth.signOut()
        _user = User.NoUserLoggedIn
    }
}

