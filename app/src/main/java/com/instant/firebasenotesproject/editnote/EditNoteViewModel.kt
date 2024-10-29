package com.instant.firebasenotesproject.editnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.instant.firebasenotesproject.model.Note
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
class EditNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditNoteUiState())
    val uiState: StateFlow<EditNoteUiState> = _uiState

    private val _effectChannel = Channel<EditNoteUiEffect>()
    val effectFlow: Flow<EditNoteUiEffect> = _effectChannel.receiveAsFlow()

    fun loadNoteDetails(noteId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            notesRepository.getNoteById(noteId).collect { result ->
                result.onSuccess { note ->
                    _uiState.value = _uiState.value.copy(
                        noteId = note.id,
                        title = note.title,
                        description = note.description,
                        date = note.date,
                        time = note.time,
                        status = note.status,
                        isLoading = false
                    )
                }.onFailure {
                    _effectChannel.send(EditNoteUiEffect.ShowError("Error loading note"))
                }
            }
        }
    }

    fun handleIntent(intent: EditNoteUiIntent) {
        when (intent) {
            is EditNoteUiIntent.EnterTitle -> _uiState.value = _uiState.value.copy(title = intent.title)
            is EditNoteUiIntent.EnterDescription -> _uiState.value = _uiState.value.copy(description = intent.description)
            is EditNoteUiIntent.EnterDate -> _uiState.value = _uiState.value.copy(date = intent.date)
            is EditNoteUiIntent.EnterTime -> _uiState.value = _uiState.value.copy(time = intent.time)
            is EditNoteUiIntent.UpdateStatus -> _uiState.value = _uiState.value.copy(status = intent.status)
            is EditNoteUiIntent.SubmitNote -> updateNote()
        }
    }

    private fun updateNote() {
        val currentUiState = _uiState.value
        if (currentUiState.title.isBlank() || currentUiState.description.isBlank()) {
            viewModelScope.launch {
                _effectChannel.send(EditNoteUiEffect.ShowError("Please fill in all fields"))
            }
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            notesRepository.updateNote(
                Note(
                    id = currentUiState.noteId,
                    title = currentUiState.title,
                    description = currentUiState.description,
                    date = currentUiState.date,
                    time = currentUiState.time,
                    status = currentUiState.status  // Includes the DELETED status if selected
                )
            ).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isNoteUpdated = true)
                    _effectChannel.send(EditNoteUiEffect.NavigateBack)
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _effectChannel.send(EditNoteUiEffect.ShowError(exception.message ?: "Error updating note"))
                }
            }
        }
    }
}
