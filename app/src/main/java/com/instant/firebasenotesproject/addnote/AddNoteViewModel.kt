package com.instant.firebasenotesproject.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instant.firebasenotesproject.model.Note
import com.instant.firebasenotesproject.model.NoteStatus
import com.instant.firebasenotesproject.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddNoteUiState())
    val uiState: StateFlow<AddNoteUiState> = _uiState

    private val _effectChannel = Channel<AddNoteUiEffect>()
    val effectFlow: Flow<AddNoteUiEffect> = _effectChannel.receiveAsFlow()

    fun handleIntent(intent: AddNoteUiIntent) {
        when (intent) {
            is AddNoteUiIntent.EnterTitle -> _uiState.value = _uiState.value.copy(title = intent.title)
            is AddNoteUiIntent.EnterDescription -> _uiState.value = _uiState.value.copy(description = intent.description)
            is AddNoteUiIntent.EnterDate -> _uiState.value = _uiState.value.copy(date = intent.date)
            is AddNoteUiIntent.EnterTime -> _uiState.value = _uiState.value.copy(time = intent.time)
            is AddNoteUiIntent.SubmitNote -> submitNote()
        }
    }

    private fun submitNote() {
        val currentUiState = _uiState.value
        if (currentUiState.title.isBlank() || currentUiState.description.isBlank()) {
            viewModelScope.launch {
                _effectChannel.send(AddNoteUiEffect.ShowError("Please fill in all fields"))
            }
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            notesRepository.addNote(
                Note(
                    title = currentUiState.title,
                    description = currentUiState.description,
                    date = currentUiState.date,
                    time = currentUiState.time,
                    status = NoteStatus.TODO  // Default status for new notes
                )
            ).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isNoteAdded = true,
                        title = "",               // Clear title after submission
                        description = "",          // Clear description after submission
                        date = "",                 // Clear date after submission
                        time = ""                  // Clear time after submission
                    )
                    _effectChannel.send(AddNoteUiEffect.NavigateBack)
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _effectChannel.send(
                        AddNoteUiEffect.ShowError(
                            exception.message ?: "Error adding note"
                        )
                    )
                }
            }
        }
    }
}
