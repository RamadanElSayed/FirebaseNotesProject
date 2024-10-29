package com.instant.firebasenotesproject.signup

import android.net.Uri


data class SignUpUiState(
    val firstName: String = "",
    val lastName: String = "",
    val jobTitle: String = "",
    val email: String = "",
    val password: String = "",
    val date: String = "",
    val time: String = "",
    val profileImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSignUpSuccess: Boolean = false,
    val errorMessage: String? = null
)


