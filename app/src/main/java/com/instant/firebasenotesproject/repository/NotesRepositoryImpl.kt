package com.instant.firebasenotesproject.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.instant.firebasenotesproject.model.Note
import com.instant.firebasenotesproject.model.NoteStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NotesRepository {

    private val notesCollection = firestore.collection("notes")

    // Add a new note to Firestore
    override suspend fun addNote(note: Note): Flow<Result<Boolean>> = flow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            emit(Result.failure(Exception("User is not logged in")))
            return@flow
        }

        try {
            // Set the current user's ID in the note
            val noteWithUserId:Note = note.copy(userId = currentUserId)

            // Create a new document in Firestore for the note
            notesCollection.document(note.id).set(noteWithUserId).await()
            emit(Result.success(true)) // Emit success result
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure result if an error occurs
        }
    }.catch { e ->
        emit(Result.failure(e)) // Handle any exception
    }

    // Update an existing note in Firestore
    override suspend fun updateNote(note: Note): Flow<Result<Boolean>> = flow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            emit(Result.failure(Exception("User is not logged in")))
            return@flow
        }

        try {
            // Ensure the note has the correct userId before updating
            val updatedNote = if (note.userId.isEmpty()) {
                note.copy(userId = currentUserId)
            } else {
                note
            }

            notesCollection.document(note.id).set(updatedNote).await()
            emit(Result.success(true)) // Emit success result
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure result if an error occurs
        }
    }.catch { e ->
        emit(Result.failure(e)) // Handle any exception
    }


    // Get a note by its ID from Firestore
    override suspend fun getNoteById(noteId: String): Flow<Result<Note>> = flow {
        try {
            val document = notesCollection.document(noteId).get().await()
            if (document.exists()) {
                val note = document.toObject(Note::class.java)
                note?.let {
                    emit(Result.success(it)) // Emit the note if found
                } ?: throw Exception("Note data is invalid")
            } else {
                throw Exception("Note not found")
            }
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure result if an error occurs
        }
    }.catch { e ->
        emit(Result.failure(e)) // Handle any exception
    }

    // Update the status of a note
    override suspend fun updateNoteStatus(noteId: String, newStatus: NoteStatus): Flow<Result<Boolean>> = flow {
        try {
            val noteRef = notesCollection.document(noteId)
            noteRef.update("status", newStatus.name).await()
            emit(Result.success(true))  // Emit success result
        } catch (e: Exception) {
            emit(Result.failure(e))  // Emit failure result in case of error
        }
    }

    // Delete a note
    override suspend fun deleteNote(noteId: String): Flow<Result<Boolean>> = flow {
        try {
            notesCollection.document(noteId).delete().await()
            emit(Result.success(true))  // Emit success result
        } catch (e: Exception) {
            emit(Result.failure(e))  // Emit failure result in case of error
        }
    }

    // Fetch all notes related to the current user only
    override suspend fun getAllNotes(): Flow<Result<List<Note>>> = flow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            emit(Result.failure(Exception("User is not logged in")))
            return@flow
        }

        try {
            // Query notes where userId matches the current user's ID
            val snapshot = notesCollection
                .whereEqualTo("userId", currentUserId)
                .get()
                .await()

            val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
            emit(Result.success(notes))  // Emit success result with notes
        } catch (e: Exception) {
            emit(Result.failure(e))  // Emit failure result in case of error
        }
    }.catch { e ->
        emit(Result.failure(e)) // Handle any exception
    }
}
