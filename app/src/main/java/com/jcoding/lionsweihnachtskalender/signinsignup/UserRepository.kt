package com.jcoding.lionsweihnachtskalender.signinsignup

import android.util.Log
import androidx.compose.runtime.Immutable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class User(open val email: String = "", open val uid: String? = null, open val displayName: String? = null, open val role: String? = null) {
    @Immutable
    data class LoggedInUser(override val email: String, override val uid: String? = null, override var displayName: String? = null, override var role: String? = null) : User() {
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


    private val database = FirebaseDatabase.getInstance()
    private val dbUserRef = database.getReference("users") // Replace "users" with your actual reference path

    private val _managedUser: MutableStateFlow<User> = MutableStateFlow(User.NoUserLoggedIn)
    val managedUser = _managedUser.asStateFlow()

    fun getManagedUser(): User {
      return _managedUser.value
    }

    fun signIn(email: String, password: String, onSignInComplete: () -> Unit) {

        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener(authListener)

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    if (auth.currentUser!= null) {
                        withContext(Dispatchers.Main) {
                            val userData = fetchUserData(dbUserRef, auth.currentUser!!.uid)
                            if (userData != null) {
                                _managedUser.value = User.LoggedInUser(
                                    managedUser.value.email,
                                    managedUser.value.uid,
                                    userData.displayName,
                                    userData.role
                                )
                            } else {
                                Log.d(
                                    "UserRepository",
                                    "User data nicht gefunden für UID: ${auth.currentUser!!.uid}"
                                )
                            }
                        }
                    }
                } catch (e: Exception){
                    Log.e("Error beim Login", e.message.toString())
                } finally {
                    withContext(Dispatchers.Main){
                        onSignInComplete()
                    }
                }

            }
        }
    }

    suspend fun fetchUserData(dbUserRef: DatabaseReference, uid: String): User.LoggedInUser? = suspendCancellableCoroutine { continuation ->
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userData = dataSnapshot.getValue(User.LoggedInUser::class.java)
                    continuation.resume(userData)
                } else {
                    Log.d("UserRepository", "User datanot found in the database for UID: $uid")
                    continuation.resume(null) // Or resume with a specific error if needed
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        }

        dbUserRef.child(uid).addListenerForSingleValueEvent(listener)

        continuation.invokeOnCancellation {
            dbUserRef.child(uid).removeEventListener(listener)
        }
    }

    fun signUp(email: String, password: String) {
        auth = FirebaseAuth.getInstance()

        // FIXME: entweder komplett implementieren oder rausnehmen
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
    }


    val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is signed in
            // You can access user information here (e.g., user.uid, user.email)
            val email = currentUser.email.toString()
            val uuid = currentUser.uid

            _managedUser.value = User.LoggedInUser(email,uuid)
            Log.d("Auth changed", "User is signed in")
        } else {
            // User is signed out
            // FIXME
            // Redirect to login screen or handle the sign-out state
            Log.d("Auth changed", "User is signed out")
            _managedUser.value = User.NoUserLoggedIn
        }
    }

    fun signInAsGuest() {
        _managedUser.value = User.GuestUser
    }

    fun signOut() {
        if (auth.currentUser != null) {
            auth.signOut()
            _managedUser.value = User.NoUserLoggedIn
        }
    }
}

