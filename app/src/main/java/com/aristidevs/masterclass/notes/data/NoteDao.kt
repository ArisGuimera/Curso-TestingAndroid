package com.aristidevs.masterclass.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getNotesFlow(): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY id DESC")
    suspend fun getNotesOnce(): List<Note>

    @Insert
    suspend fun insert(note: Note)

    @Query("DELETE FROM notes")
    suspend fun clear()
}

