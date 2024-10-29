package com.instant.firebasenotesproject.repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instant.firebasenotesproject.model.Note
import com.instant.firebasenotesproject.model.NoteStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotesRepositoryImplFirebaseDatabase @Inject constructor(
    private val database: FirebaseDatabase
) : NotesRepository {

    // Reference to the notes node in Firebase Realtime Database
    private val notesCollection: DatabaseReference = database.getReference("notes")

    // Add a new note to Firebase Realtime Database
    override suspend fun addNote(note: Note): Flow<Result<Boolean>> = flow {
        try {
            // Create a new document in Realtime Database for the note
            notesCollection.child(note.id).setValue(note).await()
            emit(Result.success(true)) // Emit success result
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure result if an error occurs
        }
    }.catch { e ->
        emit(Result.failure(e)) // Handle any exception
    }

    // Update an existing note in Firebase Realtime Database
    override suspend fun updateNote(note: Note): Flow<Result<Boolean>> = flow {
        try {
            // Update the note's document in Realtime Database
            notesCollection.child(note.id).setValue(note).await()
            emit(Result.success(true)) // Emit success result
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure result if an error occurs
        }
    }.catch { e ->
        emit(Result.failure(e)) // Handle any exception
    }

    // Get a note by its ID from Firebase Realtime Database
    override suspend fun getNoteById(noteId: String): Flow<Result<Note>> = flow {
        try {
            // Fetch the document with the given noteId
            val snapshot = notesCollection.child(noteId).get().await()
            if (snapshot.exists()) {
                // Convert the snapshot to a Note object
                val note = snapshot.getValue(Note::class.java)
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


    /*
     // Get a note by its ID from Firebase Realtime Database
    override suspend fun getNoteById(noteId: String): Flow<Result<Note>> = callbackFlow {
        try {
            // Fetch the document with the given noteId
            val noteRef = notesCollection.child(noteId)

            noteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Convert the snapshot to a Note object
                        val note = snapshot.getValue(Note::class.java)
                        note?.let {
                            trySend(Result.success(it)) // Emit the note if found
                        } ?: trySend(Result.failure(Exception("Note data is invalid")))
                    } else {
                        trySend(Result.failure(Exception("Note not found")))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Result.failure(Exception(error.message))) // Handle any error
                }
            })

            awaitClose { noteRef.removeEventListener(this) } // Remove listener when done
        } catch (e: Exception) {
            trySend(Result.failure(e)) // Emit failure result if an error occurs
        }
    }
     */

//    // Get a note by its ID from Firebase Realtime Database
//    override suspend fun getNoteById(noteId: String): Flow<Result<Note>> = callbackFlow {
//        try {
//            // Fetch the document with the given noteId
//            val noteRef = notesCollection.child(noteId)
//
//            // Create the listener as a variable
//            val listener = object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        // Convert the snapshot to a Note object
//                        val note = snapshot.getValue(Note::class.java)
//                        note?.let {
//                            trySend(Result.success(it)) // Emit the note if found
//                        } ?: trySend(Result.failure(Exception("Note data is invalid")))
//                    } else {
//                        trySend(Result.failure(Exception("Note not found")))
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    trySend(Result.failure(Exception(error.message))) // Handle any error
//                }
//            }
//
//            noteRef.addListenerForSingleValueEvent(listener)
//
//            awaitClose { noteRef.removeEventListener(listener) } // Remove listener when done
//        } catch (e: Exception) {
//            trySend(Result.failure(e)) // Emit failure result if an error occurs
//        }
//    }


    // Update the status of a note
    override suspend fun updateNoteStatus(noteId: String, newStatus: NoteStatus): Flow<Result<Boolean>> = flow {
        try {
            // Get the note document reference
            val noteRef = notesCollection.child(noteId)

            // Update the status field of the note
            noteRef.child("status").setValue(newStatus.name).await()

            emit(Result.success(true)) // Emit success result
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure result in case of error
        }
    }

    // Delete a note
    override suspend fun deleteNote(noteId: String): Flow<Result<Boolean>> = flow {
        try {
            notesCollection.child(noteId).removeValue().await()
            emit(Result.success(true)) // Emit success result
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure result in case of error
        }
    }

    // Fetch all notes
    override suspend fun getAllNotes(): Flow<Result<List<Note>>> = flow {
        try {
            val snapshot = notesCollection.get().await()
            val notes = snapshot.children.mapNotNull { it.getValue(Note::class.java) }
            emit(Result.success(notes)) // Emit success result with notes
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure result in case of error
        }
    }

//    // Listen for real-time updates of all notes
//    suspend fun listenForAllNotes(): Flow<Result<List<Note>>> = callbackFlow {
//        try {
//            val listenerRegistration = notesCollection.addValueEventListener(object :
//                ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val notes = snapshot.children.mapNotNull { it.getValue(Note::class.java) }
//                    trySend(Result.success(notes)) // Emit success result with all notes
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    trySend(Result.failure(Exception(error.message))) // Handle any error
//                }
//            })
//
//        } catch (e: Exception) {
//            trySend(Result.failure(e)) // Emit failure result if an error occurs
//        }
//    }
//
//
//
//    // Listen for real-time updates of all notes
//    suspend fun listenForAllNotes(): Flow<Result<List<Note>>> = callbackFlow {
//        try {
//            // Create the listener for real-time updates
//            val listener = object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val notes = snapshot.children.mapNotNull { it.getValue(Note::class.java) }
//                    trySend(Result.success(notes)) // Emit success result with all notes
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    trySend(Result.failure(Exception(error.message))) // Handle any error
//                }
//            }
//
//            notesCollection.addValueEventListener(listener)
//
//            awaitClose { notesCollection.removeEventListener(listener) } // Remove listener when done
//        } catch (e: Exception) {
//            trySend(Result.failure(e)) // Emit failure result if an error occurs
//        }
//    }




//    // Listen for real-time updates of all notes using ChildEventListener
//    suspend fun listenForAllNotes(): Flow<Result<List<Note>>> = callbackFlow {
//        try {
//            // Create the ChildEventListener
//            val listener = object : ChildEventListener {
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                    val note = snapshot.getValue(Note::class.java)
//                    note?.let {
//                        trySend(Result.success(listOf(it))) // Emit the new note added
//                    }
//                }
//
//                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                    val updatedNote = snapshot.getValue(Note::class.java)
//                    updatedNote?.let {
//                        trySend(Result.success(listOf(it))) // Emit the updated note
//                    }
//                }
//
//                override fun onChildRemoved(snapshot: DataSnapshot) {
//                    // Handle note removal if needed
//                    trySend(Result.failure(Exception("Note removed: ${snapshot.key}")))
//                }
//
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                    // Handle child moved event if necessary
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    trySend(Result.failure(Exception(error.message))) // Handle any error
//                }
//            }
//
//            notesCollection.addChildEventListener(listener)
//
//            awaitClose { notesCollection.removeEventListener(listener) } // Remove listener when done
//        } catch (e: Exception) {
//            trySend(Result.failure(e)) // Emit failure result if an error occurs
//        }
//    }
}
