package com.instant.firebasenotesproject.model

data class User(
    val uid: String = "",  // User's unique ID
    val firstName: String = "",
    val lastName: String = "",
    val jobTitle: String = "",
    val email: String = "",
    val date: String = "",
    val time: String = "",
    val profileImageUrl: String = ""  // Profile image URL
)


