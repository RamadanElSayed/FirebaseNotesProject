package com.instant.firebasenotesproject.home

import android.os.Message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instant.firebasenotesproject.model.NoteStatus
import com.instant.firebasenotesproject.repository.NotesRepository
import com.instant.firebasenotesproject.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _effectFlow = MutableSharedFlow<HomeUiEffect>()
    val effectFlow: SharedFlow<HomeUiEffect> = _effectFlow

    init {
        loadUserProfile()
      handleIntent(HomeUiIntent.LoadNotes)
    }

    private fun loadUserProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            userRepository.listenForUserData().collect { result ->
                result.onSuccess { user ->
                    _uiState.value = _uiState.value.copy(userProfile =user , isLoading = false)
                }.onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error loading user profile"
                    )
                    _effectFlow.emit(HomeUiEffect.ShowError("Error loading user profile"))
                }
            }
        }
    }

    fun handleIntent(intent: HomeUiIntent) {
        when (intent) {
            HomeUiIntent.LoadNotes -> loadNotes()
            HomeUiIntent.AddNote -> navigateToAddNote()
            is HomeUiIntent.UpdateNoteStatus -> updateNoteStatus(intent.noteId, intent.newStatus)
            is HomeUiIntent.DeleteNote -> deleteNote(intent.noteId)
            is HomeUiIntent.EditNote -> navigateToEditNote(intent.noteId)
            HomeUiIntent.Logout -> logout()
        }
    }


    private fun loadNotes() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            notesRepository.getAllNotes().collect { result ->
                result.onSuccess { notes ->
                    _uiState.value = _uiState.value.copy(notes = notes, isLoading = false)
                }.onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error loading notes"
                    )
                }
            }
        }
    }


    private fun updateNoteStatus(noteId: String, newStatus: NoteStatus) {
        viewModelScope.launch {
            notesRepository.updateNoteStatus(noteId, newStatus).collect { result ->
                result.onSuccess { loadNotes() }
                result.onFailure {
                    _effectFlow.emit(HomeUiEffect.ShowError("Error updating note status"))
                }
            }
        }
    }

    private fun deleteNote(noteId: String) {
        viewModelScope.launch {
            notesRepository.deleteNote(noteId).collect { result ->
                result.onSuccess { loadNotes() }
                result.onFailure {
                    _effectFlow.emit(HomeUiEffect.ShowError("Error deleting note"))
                }
            }
        }
    }

    private fun navigateToEditNote(noteId: String) {
        viewModelScope.launch {
            _effectFlow.emit(HomeUiEffect.NavigateToEditNote(noteId))
        }
    }

    private fun navigateToAddNote() {
        viewModelScope.launch {
            _effectFlow.emit(HomeUiEffect.NavigateToAddNote)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.signOut().collect { result ->
                result.onSuccess {
                    _effectFlow.emit(HomeUiEffect.NavigateToLogin)
                }.onFailure {
                    _effectFlow.emit(HomeUiEffect.ShowError("Logout failed"))
                }
            }
        }
    }
}


