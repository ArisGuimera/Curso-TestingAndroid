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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NotesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: NotesRepository = mockk()

    @Test
    fun `given an invalid title when changing the title then canSave is false`() = runTest {
        // given
        every { repository.getNotes() } returns flowOf(emptyList())
        val viewModel = NotesViewModel(repository)

        // when
        viewModel.onTitleChanged("ab")

        // then
        assertFalse(viewModel.uiState.value.canSave)
    }

    @Test
    fun `given a valid note when saving then it is added and the inputs are cleared`() = runTest {
        // given
        every { repository.getNotes() } returns flowOf(emptyList())
        coEvery { repository.addNote(any(), any(), any()) } just Runs
        val viewModel = NotesViewModel(repository)

        // when
        viewModel.onTitleChanged("  My note  ")
        viewModel.onContentChanged("Content")
        viewModel.onImportantChanged(true)
        viewModel.saveNote()

        // then
        coVerify { repository.addNote("My note", "Content", true) }
        assertEquals("", viewModel.uiState.value.title)
    }

    @Test
    fun `given a note when deleting then it is removed from the repository`() = runTest {
        // given
        every { repository.getNotes() } returns flowOf(emptyList())
        coEvery { repository.deleteNote(any()) } just Runs
        val viewModel = NotesViewModel(repository)
        val note = Note(id = 7, title = "To delete", content = "", important = false)

        // when
        viewModel.onDeleteNote(note)

        // then
        coVerify { repository.deleteNote(note) }
    }

    @Test
    fun `given the repository emits notes when observing then the state is updated`() = runTest {
        // given
        val notesFlow = MutableStateFlow(emptyList<Note>())
        every { repository.getNotes() } returns notesFlow
        val viewModel = NotesViewModel(repository)

        // when / then
        viewModel.uiState.test {
            assertEquals(0, awaitItem().notes.size)

            notesFlow.value = listOf(Note(id = 1, title = "A", content = "", important = true))

            assertEquals(1, awaitItem().notes.size)
            cancelAndConsumeRemainingEvents()
        }
    }

}






