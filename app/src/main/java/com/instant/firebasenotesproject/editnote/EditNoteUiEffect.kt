package com.instant.firebasenotesproject.editnote

sealed class EditNoteUiEffect {
    data object NavigateBack : EditNoteUiEffect()  // Trigger navigation when the note is updated
    data class ShowError(val message: String) : EditNoteUiEffect()  // Show error message
}
