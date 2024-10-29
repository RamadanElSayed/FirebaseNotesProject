package com.instant.firebasenotesproject.home

import com.instant.firebasenotesproject.model.Note
import com.instant.firebasenotesproject.model.User

data class HomeUiState(
    val notes: List<Note> = emptyList(),  // List of notes to be displayed
    val isLoading: Boolean = false,       // Loading state
    val errorMessage: String? = null,
    val userProfile: User? = null
    // Error message if something goes wrong
)