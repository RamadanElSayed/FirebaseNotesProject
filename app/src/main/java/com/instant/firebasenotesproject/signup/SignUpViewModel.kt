package com.instant.firebasenotesproject.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instant.firebasenotesproject.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    private val _effectFlow = MutableSharedFlow<SignUpUiEffect>()
    val effectFlow: SharedFlow<SignUpUiEffect> = _effectFlow

    fun handleIntent(intent: SignUpUiIntent) {
        when (intent) {
            is SignUpUiIntent.EnterFirstName -> _uiState.update { it.copy(firstName = intent.firstName) }
            is SignUpUiIntent.EnterLastName -> _uiState.update { it.copy(lastName = intent.lastName) }
            is SignUpUiIntent.EnterJobTitle -> _uiState.update { it.copy(jobTitle = intent.jobTitle) }
            is SignUpUiIntent.EnterEmail -> _uiState.update { it.copy(email = intent.email) }
            is SignUpUiIntent.EnterPassword -> _uiState.update { it.copy(password = intent.password) }
            is SignUpUiIntent.EnterDate -> _uiState.update { it.copy(date = intent.date) }
            is SignUpUiIntent.EnterTime -> _uiState.update { it.copy(time = intent.time) }
            is SignUpUiIntent.SelectProfileImage -> _uiState.update { it.copy(profileImageUri = intent.uri) }
            SignUpUiIntent.SubmitSignUp -> submitSignUp()
        }
    }

    private fun submitSignUp() {
        val imageUri = _uiState.value.profileImageUri

        if (imageUri == null) {
            _uiState.update {
                it.copy(
                    errorMessage = "Please select a profile image",
                    isLoading = false
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        uploadProfileImage(imageUri)
    }

    private fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            userRepository.uploadProfileImage(imageUri).collect { result ->
                result.onSuccess { url ->
                    if (url.isNotEmpty()) {
                        // Successful upload and URL is valid; proceed with sign-up
                        signUpUser(url)
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Image upload failed: URL is empty."
                            )
                        }
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Image upload failed: ${error.message}"
                        )
                    }
                }
            }
        }
    }

    private fun signUpUser(profileImageUrl: String) {
        val currentUiState = _uiState.value

        if (profileImageUrl.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Profile image URL is empty. Please select a valid image."
                )
            }
            return
        }

        viewModelScope.launch {
            userRepository.signUp(
                email = currentUiState.email,
                password = currentUiState.password,
                firstName = currentUiState.firstName,
                lastName = currentUiState.lastName,
                jobTitle = currentUiState.jobTitle,
                date = currentUiState.date,
                time = currentUiState.time,
                profileImageUrl = profileImageUrl
            ).collect { result ->
                result.onSuccess {
                    fetchUserDataAndNavigate()
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
            }
        }
    }

    private fun fetchUserDataAndNavigate() {
        viewModelScope.launch {
            userRepository.listenForUserData().collect { result ->
                result.onSuccess { user ->
                    _effectFlow.emit(SignUpUiEffect.NavigateToHome(user.firstName, user.jobTitle))
                }.onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }
}
