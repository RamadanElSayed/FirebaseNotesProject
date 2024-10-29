package com.instant.firebasenotesproject.addnote

sealed class AddNoteUiIntent {
    data class EnterTitle(val title: String) : AddNoteUiIntent()
    data class EnterDescription(val description: String) : AddNoteUiIntent()
    data class EnterDate(val date: String) : AddNoteUiIntent()
    data class EnterTime(val time: String) : AddNoteUiIntent()
    data object SubmitNote : AddNoteUiIntent()  // Action to submit the note
}
