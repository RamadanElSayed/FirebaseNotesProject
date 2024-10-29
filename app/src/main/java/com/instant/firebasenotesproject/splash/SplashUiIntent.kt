package com.instant.firebasenotesproject.splash

sealed class SplashUiIntent {
    data object CheckUserStatus : SplashUiIntent()  // Intent to check if the user is logged in or not
}
