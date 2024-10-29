package com.instant.firebasenotesproject.navigation

import kotlinx.serialization.Serializable

// Serializable route objects
@Serializable
data object SplashRoute

@Serializable
data object SignInRoute

@Serializable
data object SignUpRoute

@Serializable
data class HomeRoute(val fullName: String, val jobTitle: String)

@Serializable
data class AddNoteRoute(val fullName: String, val jobTitle: String)

@Serializable
data class EditNoteRoute(val noteId: String, val fullName: String, val jobTitle: String)