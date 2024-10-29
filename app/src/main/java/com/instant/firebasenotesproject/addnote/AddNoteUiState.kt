package com.instant.firebasenotesproject.addnote

data class AddNoteUiState(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val isLoading: Boolean = false,
    val isNoteAdded: Boolean = false, // To track when the note has been added
    val errorMessage: String? = null
)
