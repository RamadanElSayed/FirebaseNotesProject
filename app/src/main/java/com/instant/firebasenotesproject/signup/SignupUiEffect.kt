package com.instant.firebasenotesproject.signup

import com.instant.firebasenotesproject.signin.LoginUiEffect

// SnackbarEvent for one-time snackbar events
sealed class SignUpUiEffect {
    data class NavigateToHome(val fullName: String, val jobTitle: String) : SignUpUiEffect()
    data class ShowSnackbar(val message: String) : SignUpUiEffect()
}

