package com.instant.firebasenotesproject.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instant.firebasenotesproject.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // UI state for the login screen
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // SharedFlow for navigation or effects
    private val _effectFlow = MutableSharedFlow<LoginUiEffect>()
    val effectFlow: SharedFlow<LoginUiEffect> = _effectFlow

    // Handles user input and actions
    fun handleIntent(intent: LoginUiIntent) {
        when (intent) {
            is LoginUiIntent.EnterEmail -> {
                _uiState.value = _uiState.value.copy(email = intent.email)
            }

            is LoginUiIntent.EnterPassword -> {
                _uiState.value = _uiState.value.copy(password = intent.password)
            }

            is LoginUiIntent.SubmitLogin -> {
                submitLogin()
            }
        }
    }

    // Trigger the login process
    private fun submitLogin() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            userRepository.signIn(
                email = _uiState.value.email,
                password = _uiState.value.password
            ).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    // Fetch user data and navigate to home
                    fetchUserDataAndNavigate()
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            }
        }
    }

    // Fetch user data from Firestore and emit effect for navigation
    private fun fetchUserDataAndNavigate() {
        _uiState.value = _uiState.value.copy(isLoading = true)  // Start loading

        viewModelScope.launch {
            userRepository.listenForUserData().collect { result ->
                result.onSuccess { user ->
                    _uiState.value = _uiState.value.copy(isLoading = false)  // Stop loading
                    _effectFlow.emit(LoginUiEffect.NavigateToHome(user.firstName, user.jobTitle))
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,  // Stop loading in case of error
                        errorMessage = error.message
                    )
                }
            }
        }
    }
}

