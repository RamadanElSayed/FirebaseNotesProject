package com.instant.firebasenotesproject.signup

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.instant.firebasenotesproject.components.ProfilePictureSelector
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: (String, String) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // State for date and time pickers
    var date by remember { mutableStateOf(uiState.date) }
    var time by remember { mutableStateOf(uiState.time) }

    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    // DatePicker Dialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            date = dateFormatter.format(calendar.time)
            viewModel.handleIntent(SignUpUiIntent.EnterDate(date))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // TimePicker Dialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            time = timeFormatter.format(calendar.time)
            viewModel.handleIntent(SignUpUiIntent.EnterTime(time))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Collect effect and handle navigation
    LaunchedEffect(viewModel) {
        viewModel.effectFlow.collectLatest { effect ->
            when (effect) {
                is SignUpUiEffect.NavigateToHome -> onNavigateToHome(effect.fullName, effect.jobTitle)
                is SignUpUiEffect.ShowSnackbar -> { /* Handle snackbar if needed */ }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)

    ) {

        // Main UI content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.Center).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Sign Up",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Profile Picture Selector
            ProfilePictureSelector(
                onImageSelected = { uri ->
                    viewModel.handleIntent(SignUpUiIntent.SelectProfileImage(uri))
                }
            )

            // First Name Input
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = { viewModel.handleIntent(SignUpUiIntent.EnterFirstName(it)) },
                label = { Text("First Name") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Last Name Input
            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = { viewModel.handleIntent(SignUpUiIntent.EnterLastName(it)) },
                label = { Text("Last Name") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Job Title Input
            OutlinedTextField(
                value = uiState.jobTitle,
                onValueChange = { viewModel.handleIntent(SignUpUiIntent.EnterJobTitle(it)) },
                label = { Text("Job Title") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Email Input
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.handleIntent(SignUpUiIntent.EnterEmail(it)) },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Password Input
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.handleIntent(SignUpUiIntent.EnterPassword(it)) },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Date Picker Button
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = if (date.isNotEmpty()) date else "Select Date")
            }

            // Time Picker Button
            OutlinedButton(
                onClick = { timePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = if (time.isNotEmpty()) time else "Select Time")
            }

            // Sign-Up Button
            Button(
                onClick = { viewModel.handleIntent(SignUpUiIntent.SubmitSignUp) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text("Sign Up", color = Color.White)
            }

            // Navigate to Login Screen
            Text(
                text = "Already have an account? Log In",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .clickable { onNavigateToLogin() },
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        // Loader at the top-center above the UI
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)) // Optional dim background
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

    }

}
