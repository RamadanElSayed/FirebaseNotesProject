package com.instant.firebasenotesproject.addnote

sealed class AddNoteUiEffect {
    data object NavigateBack : AddNoteUiEffect()  // Trigger navigation when the note is added
    data class ShowError(val message: String) : AddNoteUiEffect()  // Show error message
}
