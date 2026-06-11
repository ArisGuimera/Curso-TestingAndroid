package com.aristidevs.masterclass.notes.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class NoteDaoTest {

    private lateinit var db: NotesDatabase
    private lateinit var dao: NoteDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java).build()
        dao = db.noteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insert_and_query_returns_notes_in_desc_order() = runTest {
        dao.insert(Note(title = "Primera", content = "", important = false))
        dao.insert(Note(title = "Segunda", content = "", important = true))

        val notes = dao.getNotesOnce()

        assertEquals(2, notes.size)
        assertEquals("Segunda", notes[0].title)
        assertEquals("Primera", notes[1].title)
    }

    @Test
    fun delete_removes_note_from_db() = runTest {
        dao.insert(Note(title = "Borrable", content = "", important = false))
        val inserted = dao.getNotesOnce().first()

        dao.delete(inserted)

        assertEquals(0, dao.getNotesOnce().size)
    }
}
