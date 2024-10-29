package com.instant.firebasenotesproject.signin

sealed class LoginUiIntent {
    data class EnterEmail(val email: String) : LoginUiIntent()
    data class EnterPassword(val password: String) : LoginUiIntent()
    object SubmitLogin : LoginUiIntent()
}
