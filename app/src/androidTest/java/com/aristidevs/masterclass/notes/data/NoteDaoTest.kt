package com.aristidevs.masterclass.notes.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
        db = Room.inMemoryDatabaseBuilder(
            context,
            NotesDatabase::class.java
        ).build()
        dao = db.noteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insert_and_query_returns_in_expected_order() = runTest {
        val first = Note(title = "Primera", content = "", important = false)
        val second = Note(title = "Segunda", content = "", important = true)

        dao.insert(first)
        dao.insert(second)

        val notes = dao.getNotesOnce()

        assertEquals(2, notes.size)
        // Orden DESC por id: la segunda va primero
        assertEquals("Segunda", notes[0].title)
        assertEquals("Primera", notes[1].title)
    }

    @Test
    fun insert_and_query_with_flow_returns_in_expected_order() = runTest {
        val first = Note(title = "Primera", content = "", important = false)
        val second = Note(title = "Segunda", content = "", important = true)

        dao.insert(first)
        dao.insert(second)

        // Usamos .first() para tomar la primera emisión del Flow y cerrar la suscripción
        val notes = dao.getNotesFlow().first()

        assertEquals(2, notes.size)
        assertEquals("Segunda", notes[0].title)
        assertEquals("Primera", notes[1].title)
    }
}

