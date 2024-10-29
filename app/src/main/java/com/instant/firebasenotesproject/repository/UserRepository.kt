package com.instant.firebasenotesproject.repository

import android.net.Uri
import com.instant.firebasenotesproject.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // Sign up with additional user details, now using a URL for the profile image
    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        jobTitle: String,
        date: String,
        time: String,
        profileImageUrl: String // Use profile image URL instead of Uri
    ): Flow<Result<Boolean>>

    // Sign up with just email and password
    suspend fun signUp(email: String, password: String): Flow<Result<Boolean>>

    // Function to upload profile image and return its URL
    suspend fun uploadProfileImage(imageUri: Uri): Flow<Result<String>>

    // Sign in with email and password
    suspend fun signIn(email: String, password: String): Flow<Result<Boolean>>

    // Sign out the current user
    suspend fun signOut(): Flow<Result<Boolean>>

    // Check if the user is currently logged in
    fun isUserLoggedIn(): Flow<Boolean>

    // Fetch user data from Firestore
    suspend fun listenForUserData(): Flow<Result<User>>
}
