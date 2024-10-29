package com.instant.firebasenotesproject.signup

import android.net.Uri


// Intent representing user actions

// Intent representing user actions
sealed class SignUpUiIntent {
    data class EnterFirstName(val firstName: String) : SignUpUiIntent()
    data class EnterLastName(val lastName: String) : SignUpUiIntent()
    data class EnterJobTitle(val jobTitle: String) : SignUpUiIntent()
    data class EnterEmail(val email: String) : SignUpUiIntent()
    data class EnterPassword(val password: String) : SignUpUiIntent()
    data class EnterDate(val date: String) : SignUpUiIntent()
    data class EnterTime(val time: String) : SignUpUiIntent()
    data class SelectProfileImage(val uri: Uri?) : SignUpUiIntent()
    data object SubmitSignUp : SignUpUiIntent()
}
