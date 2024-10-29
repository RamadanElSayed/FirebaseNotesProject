package com.instant.firebasenotesproject.repository

import com.instant.firebasenotesproject.model.Note
import com.instant.firebasenotesproject.model.NoteStatus
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    // Add a new note to Firestore
    suspend fun addNote(note: Note): Flow<Result<Boolean>>

    // Update an existing note in Firestore
    suspend fun updateNote(note: Note): Flow<Result<Boolean>>

    // Get a note by its ID from Firestore
    suspend fun getNoteById(noteId: String): Flow<Result<Note>>
    // Update the status of a note
    suspend fun updateNoteStatus(noteId: String, newStatus: NoteStatus): Flow<Result<Boolean>>

    // Delete a note
    suspend fun deleteNote(noteId: String): Flow<Result<Boolean>>

    // Fetch all notes
    suspend fun getAllNotes(): Flow<Result<List<Note>>>
}
