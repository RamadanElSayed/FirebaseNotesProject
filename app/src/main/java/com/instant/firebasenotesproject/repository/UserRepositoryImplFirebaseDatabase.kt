package com.instant.firebasenotesproject.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.instant.firebasenotesproject.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImplFirebaseDatabase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) : UserRepository {

    // Reference to the users node in Firebase Realtime Database
    private val usersReference: DatabaseReference = database.getReference("users")

    // Sign up with additional user details and optional profile image URL
    override suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        jobTitle: String,
        date: String,
        time: String,
        profileImageUrl: String // Now accepts a URL rather than handling the upload
    ): Flow<Result<Boolean>> = flow {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID not found")

            val user = User(
                uid = uid,
                firstName = firstName,
                lastName = lastName,
                jobTitle = jobTitle,
                email = email,
                date = date,
                time = time,
                profileImageUrl = profileImageUrl
            )

            // Store user data in Firebase Realtime Database
            usersReference.child(uid).setValue(user).await()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Upload profile image to Firebase Storage and return its URL
    override suspend fun uploadProfileImage(imageUri: Uri): Flow<Result<String>> = flow {
        try {
            // Reference to where the image will be saved in Firebase Storage
            val storageRef = storage.reference.child("profile_pictures/${imageUri.lastPathSegment}")

            // Upload file to Firebase Storage and await completion
            storageRef.putFile(imageUri).await()

            // Fetch the download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            emit(Result.success(downloadUrl))

        } catch (e: Exception) {
            // Log the error message if the upload fails
            Log.e("UserRepositoryImpl", "Error uploading image: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    // Basic sign-up with email and password
    override suspend fun signUp(email: String, password: String): Flow<Result<Boolean>> = flow {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    // Sign in with Firebase
    override suspend fun signIn(email: String, password: String): Flow<Result<Boolean>> = flow {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    // Sign out with Firebase Authentication
    override suspend fun signOut(): Flow<Result<Boolean>> = flow {
        try {
            firebaseAuth.signOut()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    // Check if user is logged in
    override fun isUserLoggedIn(): Flow<Boolean> = flow {
        emit(firebaseAuth.currentUser != null)
    }

    // Fetch user data from Realtime Database once
     suspend fun getUserData(): Flow<Result<User>> = flow {
        try {
            val uid = firebaseAuth.currentUser?.uid ?: throw Exception("User not logged in")
            val snapshot = usersReference.child(uid).get().await()

            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    emit(Result.success(it))
                } ?: throw Exception("User data is invalid")
            } else {
                emit(Result.failure(Exception("User data not found")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.catch { e ->
        emit(Result.failure(e))
    }

    // Listen for real-time updates of user data
// Listen for real-time updates of user data
  override  suspend fun listenForUserData(): Flow<Result<User>> = callbackFlow {
        try {
            val uid = firebaseAuth.currentUser?.uid ?: throw Exception("User not logged in")

            // Create the ValueEventListener as a variable
            val userValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            trySend(Result.success(it))
                        } ?: trySend(Result.failure(Exception("User data is invalid")))
                    } else {
                        trySend(Result.failure(Exception("User data not found")))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.failure(Exception(error.message)))
                }
            }

            // Listen for changes in the user's node
            usersReference.child(uid).addValueEventListener(userValueEventListener)

            // Await closure to remove the listener
            awaitClose { usersReference.child(uid).removeEventListener(userValueEventListener) }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
    }

}
