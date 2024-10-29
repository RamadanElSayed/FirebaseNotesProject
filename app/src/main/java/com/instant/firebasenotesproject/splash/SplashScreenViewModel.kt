package com.instant.firebasenotesproject.splash

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
class SplashScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // State to represent the loading state of the splash screen
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState

    // SharedFlow for effects like navigation
    private val _effectFlow = MutableSharedFlow<SplashUiEffect>()
    val effectFlow: SharedFlow<SplashUiEffect> = _effectFlow

    // Handle the intents coming from the SplashScreen
    fun handleIntent(intent: SplashUiIntent) {
        when (intent) {
            is SplashUiIntent.CheckUserStatus -> checkUserStatus()
        }
    }

    // Check if the user is logged in and decide the navigation route
    private fun checkUserStatus() {
        _uiState.value = SplashUiState(isLoading = true)  // Show loading

        viewModelScope.launch {
            userRepository.isUserLoggedIn().collect { isLoggedIn ->
                if (isLoggedIn) {
                    // Fetch user data and navigate to home if logged in
                    userRepository.listenForUserData().collect { result ->
                        result.onSuccess { user ->
                            _effectFlow.emit(
                                SplashUiEffect.NavigateToHome(
                                    user.firstName,
                                    user.jobTitle
                                )
                            )
                        }.onFailure {
                            // On failure to fetch user data, navigate to login
                            _effectFlow.emit(SplashUiEffect.NavigateToLogin)
                        }
                    }
                } else {
                    // If the user is not logged in, navigate to the login screen
                    _effectFlow.emit(SplashUiEffect.NavigateToLogin)
                }
            }
        }
    }
}

