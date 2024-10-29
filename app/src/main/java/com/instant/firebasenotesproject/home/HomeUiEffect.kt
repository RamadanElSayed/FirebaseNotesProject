package com.instant.firebasenotesproject.home

sealed class HomeUiEffect {
    data class NavigateToEditNote(val noteId: String) : HomeUiEffect()
    data object NavigateToAddNote : HomeUiEffect()
    data object NavigateToLogin : HomeUiEffect()  // Navigation effect to login screen after logout
    data class ShowError(val message: String) : HomeUiEffect()
}
