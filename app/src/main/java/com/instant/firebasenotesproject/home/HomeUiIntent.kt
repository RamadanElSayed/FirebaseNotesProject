package com.instant.firebasenotesproject.home

import com.instant.firebasenotesproject.model.NoteStatus

sealed class HomeUiIntent {
    data object LoadNotes : HomeUiIntent()
    data object AddNote : HomeUiIntent()
    data class UpdateNoteStatus(val noteId: String, val newStatus: NoteStatus) : HomeUiIntent()
    data class DeleteNote(val noteId: String) : HomeUiIntent()
    data class EditNote(val noteId: String) : HomeUiIntent()
    data object Logout : HomeUiIntent()  // Logout intent
}

