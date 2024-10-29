package com.instant.firebasenotesproject.editnote

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
import com.instant.firebasenotesproject.model.NoteStatus
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: String,
    onNavigateBack: () -> Unit,  // Lambda to handle back navigation after updating note
    viewModel: EditNoteViewModel = hiltViewModel()
) {
    // Load the note details when the screen is shown
    LaunchedEffect(noteId) {
        viewModel.loadNoteDetails(noteId)
    }

    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Handle side effects like navigation and showing errors
    LaunchedEffect(viewModel) {
        viewModel.effectFlow.collectLatest { effect ->
            when (effect) {
                is EditNoteUiEffect.NavigateBack -> onNavigateBack()
                is EditNoteUiEffect.ShowError -> {
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
                title = { Text("Edit Note") },
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title Input
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.handleIntent(EditNoteUiIntent.EnterTitle(it)) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description Input
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.handleIntent(EditNoteUiIntent.EnterDescription(it)) },
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

                // Show DatePickerModal if triggered
                if (showDatePicker) {
                    DatePickerModal(
                        onDateSelected = { dateMillis ->
                            dateMillis?.let {
                                val selectedDate = dateFormatter.format(Date(it))
                                viewModel.handleIntent(EditNoteUiIntent.EnterDate(selectedDate))
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

                // Show TimePickerModal if triggered
                if (showTimePicker) {
                    TimePickerComponent(
                        onConfirm = { hour, minute ->
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                            }
                            val selectedTime = timeFormatter.format(calendar.time)
                            viewModel.handleIntent(EditNoteUiIntent.EnterTime(selectedTime))
                            showTimePicker = false
                        },
                        onDismiss = { showTimePicker = false }
                    )
                }

                // Status Dropdown (Including the Deleted status)
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Status: ${uiState.status}")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("TODO") },
                            onClick = {
                                expanded = false
                                viewModel.handleIntent(EditNoteUiIntent.UpdateStatus(NoteStatus.TODO))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("In Progress") },
                            onClick = {
                                expanded = false
                                viewModel.handleIntent(EditNoteUiIntent.UpdateStatus(NoteStatus.IN_PROGRESS))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Done") },
                            onClick = {
                                expanded = false
                                viewModel.handleIntent(EditNoteUiIntent.UpdateStatus(NoteStatus.DONE))
                            }
                        )
                    }
                }

                // Submit Button
                Button(
                    onClick = { viewModel.handleIntent(EditNoteUiIntent.SubmitNote) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Update Note")
                    }
                }
            }
        }
    )
}

