package com.instant.firebasenotesproject.editnote

import com.instant.firebasenotesproject.model.NoteStatus

data class EditNoteUiState(
    val noteId: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val status: NoteStatus = NoteStatus.TODO,
    val isLoading: Boolean = false,
    val isNoteUpdated: Boolean = false, // To track when the note has been updated
    val errorMessage: String? = null
)
