package com.instant.firebasenotesproject.model

import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),  // Unique ID for each note
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val status: NoteStatus = NoteStatus.TODO,  // Default status
    val userId: String = ""  // ID of the user who created the note
)


