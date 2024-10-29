package com.instant.firebasenotesproject.model

// UserProfile.kt
data class UserProfile(
    val fullName: String = "",
    val jobTitle: String = "",
    val profileImageUri: String? = null // Optional URI for the profile image
)
