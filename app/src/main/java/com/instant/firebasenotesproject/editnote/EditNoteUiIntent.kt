package com.instant.firebasenotesproject.editnote

import com.instant.firebasenotesproject.model.NoteStatus

sealed class EditNoteUiIntent {
    data class EnterTitle(val title: String) : EditNoteUiIntent()
    data class EnterDescription(val description: String) : EditNoteUiIntent()
    data class EnterDate(val date: String) : EditNoteUiIntent()
    data class EnterTime(val time: String) : EditNoteUiIntent()
    data class UpdateStatus(val status: NoteStatus) : EditNoteUiIntent()
    data object SubmitNote : EditNoteUiIntent()  // Action to submit the updated note
}
