package com.aristidevs.masterclass.notes.data

import com.aristidevs.masterclass.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeNotesRepository : NotesRepository {

    private val notes = mutableListOf<Note>()
    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())

    override fun getNotes(): Flow<List<Note>> = notesFlow.asStateFlow()

    override suspend fun addNote(title: String, content: String, important: Boolean) {
        val nextId = (notes.maxOfOrNull { it.id } ?: 0L) + 1L
        val note = Note(
            id = nextId,
            title = title,
            content = content,
            important = important
        )
        notes.add(0, note)
        notesFlow.value = notes.toList()
    }

    override suspend fun clear() {
        notes.clear()
        notesFlow.value = emptyList()
    }
}

