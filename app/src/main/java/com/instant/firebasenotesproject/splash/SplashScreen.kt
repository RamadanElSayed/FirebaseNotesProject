package com.instant.firebasenotesproject.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,  // Callback to navigate to the login screen
    onNavigateToHome: (String, String) -> Unit,  // Callback to navigate to the home screen
    viewModel: SplashScreenViewModel = hiltViewModel()  // Splash screen view model
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // Collect effects and handle navigation
    LaunchedEffect(Unit) {
        viewModel.effectFlow.collectLatest { effect ->
            when (effect) {
                is SplashUiEffect.NavigateToLogin -> onNavigateToLogin()
                is SplashUiEffect.NavigateToHome -> onNavigateToHome(effect.fullName, effect.jobTitle)
            }
        }
    }

    // Send the intent to check user status after a delay
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            delay(2000)  // Simulate delay
            viewModel.handleIntent(SplashUiIntent.CheckUserStatus)
        }
    }

    // UI for splash screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            Text(
                text = "Welcome to Firebase Notes!",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
