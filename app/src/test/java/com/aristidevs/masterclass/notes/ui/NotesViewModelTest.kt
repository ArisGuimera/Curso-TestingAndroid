package com.aristidevs.masterclass.notes.ui

import app.cash.turbine.test
import com.aristidevs.masterclass.notes.MainDispatcherRule
import com.aristidevs.masterclass.notes.data.Note
import com.aristidevs.masterclass.notes.domain.NotesRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: NotesRepository = mockk(relaxed = true)

    @Test
    fun canSave_is_false_when_title_invalid() = runTest {
        every { repository.getNotes() } returns flowOf(emptyList())
        val viewModel = NotesViewModel(repository)

        viewModel.onTitleChanged("ab")

        assertFalse(viewModel.uiState.value.canSave)
    }

    @Test
    fun saving_valid_note_calls_repository_and_clears_inputs() = runTest {
        every { repository.getNotes() } returns flowOf(emptyList())
        coEvery { repository.addNote(any(), any(), any()) } just Runs
        val viewModel = NotesViewModel(repository)

        viewModel.onTitleChanged("  Mi nota  ")
        viewModel.onContentChanged("Contenido")
        viewModel.onImportantChanged(true)
        viewModel.saveNote()

        // El título se normaliza (trim) antes de guardar.
        coVerify { repository.addNote("Mi nota", "Contenido", true) }
        assertEquals("", viewModel.uiState.value.title)
    }

    @Test
    fun state_emits_notes_when_repository_emits() = runTest {
        val notesFlow = MutableStateFlow(emptyList<Note>())
        every { repository.getNotes() } returns notesFlow
        val viewModel = NotesViewModel(repository)

        viewModel.uiState.test {
            assertEquals(0, awaitItem().notes.size)

            notesFlow.value = listOf(Note(id = 1, title = "A", content = "", important = true))

            assertEquals(1, awaitItem().notes.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun deleting_note_calls_repository() = runTest {
        every { repository.getNotes() } returns flowOf(emptyList())
        coEvery { repository.deleteNote(any()) } just Runs
        val viewModel = NotesViewModel(repository)
        val note = Note(id = 7, title = "Para borrar", content = "", important = false)

        viewModel.onDeleteNote(note)

        coVerify { repository.deleteNote(note) }
    }
}
