package com.aristidevs.masterclass.notes.domain

import com.aristidevs.masterclass.notes.data.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getNotes(): Flow<List<Note>>
    suspend fun addNote(title: String, content: String, important: Boolean)
    suspend fun clear()
}

