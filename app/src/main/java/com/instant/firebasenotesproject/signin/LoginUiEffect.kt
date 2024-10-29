package com.instant.firebasenotesproject.signin

// SnackbarEvent for one-time snackbar events
sealed class LoginUiEffect {
    data class NavigateToHome(val fullName: String, val jobTitle: String) : LoginUiEffect()
    data class ShowSnackbar(val message: String) : LoginUiEffect()
}
