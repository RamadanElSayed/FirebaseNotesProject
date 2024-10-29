package com.instant.firebasenotesproject.addnote

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.instant.firebasenotesproject.components.DatePickerModal
import com.instant.firebasenotesproject.components.TimePickerComponent
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    onNavigateBack: () -> Unit,  // Lambda to handle back navigation after adding note
    viewModel: AddNoteViewModel = hiltViewModel()
) {
    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Handle side effects like navigation and showing errors
    LaunchedEffect(viewModel) {
        viewModel.effectFlow.collectLatest { effect ->
            when (effect) {
                is AddNoteUiEffect.NavigateBack -> onNavigateBack()
                is AddNoteUiEffect.ShowError -> {
                    // Show snackbar or error message here
                }
            }
        }
    }

    // Date and Time picker values
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Note") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title Input
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.handleIntent(AddNoteUiIntent.EnterTitle(it)) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description Input
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.handleIntent(AddNoteUiIntent.EnterDescription(it)) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date Picker Button
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (uiState.date.isNotEmpty()) uiState.date else "Select Date")
                }

                // Show DatePicker if triggered
                if (showDatePicker) {
                    DatePickerModal(
                        onDateSelected = { dateMillis ->
                            dateMillis?.let {
                                val selectedDate = dateFormatter.format(Date(it))
                                viewModel.handleIntent(AddNoteUiIntent.EnterDate(selectedDate))
                            }
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }

                // Time Picker Button
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (uiState.time.isNotEmpty()) uiState.time else "Select Time")
                }

                // Show TimePicker if triggered
                if (showTimePicker) {
                    TimePickerComponent(
                        onConfirm = { hour, minute ->
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                            }
                            val selectedTime = timeFormatter.format(calendar.time)
                            viewModel.handleIntent(AddNoteUiIntent.EnterTime(selectedTime))
                            showTimePicker = false
                        },
                        onDismiss = { showTimePicker = false }
                    )
                }

                // Submit Button
                Button(
                    onClick = { viewModel.handleIntent(AddNoteUiIntent.SubmitNote) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Submit Note")
                    }
                }
            }
        }
    )
}
