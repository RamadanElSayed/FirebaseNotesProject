package com.instant.firebasenotesproject.splash

sealed class SplashUiEffect {
    data object NavigateToLogin : SplashUiEffect()  // Effect for navigating to the login screen
    data class NavigateToHome(val fullName: String, val jobTitle: String) : SplashUiEffect()  // Effect for navigating to the home screen with user data
}
