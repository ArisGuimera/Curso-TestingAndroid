package com.aristidevs.masterclass.notes.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var db: NotesDatabase
    private lateinit var dao: NoteDao

    @Before
    fun setUp(){
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java).build()
        dao = db.noteDao()
    }

    @After
    fun tearDown(){
        db.close()
    }

    @Test
    fun givenTwoNotes_whenQuerying_thenTheyAreReturnedInDescendingOrder() = runTest{
        dao.insert(Note(title = "prueba 1", content = "", important = false))
        dao.insert(Note(title = "prueba 2", content = "", important = true))

        val notes = dao.getNotesOnce()

        assertEquals(2, notes.size)
        assertEquals("prueba 2", notes[0].title)
        assertEquals("prueba 1", notes[1].title)
    }

    @Test
    fun givenANote_whenDeleting_thenItIsRemoveFromTheDatabase() = runTest {
        //given
        dao.insert(Note(title = "prueba 1", content = "", important = false))
        val note = dao.getNotesOnce().first()

        dao.delete(note)

        assertEquals(0, dao.getNotesOnce().size)

    }

}