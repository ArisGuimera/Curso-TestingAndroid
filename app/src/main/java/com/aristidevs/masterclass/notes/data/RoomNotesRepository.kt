package com.aristidevs.masterclass.notes.data

import com.aristidevs.masterclass.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow

class RoomNotesRepository(
    private val noteDao: NoteDao
) : NotesRepository {

    override fun getNotes(): Flow<List<Note>> = noteDao.getNotesFlow()

    override suspend fun addNote(title: String, content: String, important: Boolean) {
        val note = Note(
            title = title,
            content = content,
            important = important
        )
        noteDao.insert(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    override suspend fun clear() {
        noteDao.clear()
    }
}

