package com.aristidevs.masterclass.notes.ui

import com.aristidevs.masterclass.notes.MainDispatcherRule
import com.aristidevs.masterclass.notes.data.FakeNotesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun canSave_is_false_when_title_invalid() = runTest {
        val viewModel = NotesViewModel(FakeNotesRepository())

        viewModel.onTitleChanged("ab") // inválido

        val state = viewModel.uiState.value
        assertFalse(state.canSave)
    }

    @Test
    fun save_with_valid_title_inserts_clears_inputs_and_updates_state() = runTest {
        val viewModel = NotesViewModel(FakeNotesRepository())

        viewModel.onTitleChanged("  Mi nota  ")
        viewModel.onContentChanged("Contenido")
        viewModel.onImportantChanged(true)

        viewModel.saveNote()

        val state = viewModel.uiState.value
        assertEquals("", state.title)
        assertEquals("", state.content)
        assertFalse(state.important)
        assertEquals(1, state.notes.size)
        assertEquals("Mi nota", state.notes.first().title)
        assertTrue(state.notes.first().important)
    }

    @Test
    fun important_only_filter_works_on_state() = runTest {
        val viewModel = NotesViewModel(FakeNotesRepository())

        viewModel.onTitleChanged("Normal")
        viewModel.onImportantChanged(false)
        viewModel.saveNote()

        viewModel.onTitleChanged("Importante")
        viewModel.onImportantChanged(true)
        viewModel.saveNote()

        // Sin filtro: deben aparecer las dos
        var state = viewModel.uiState.value
        assertEquals(2, state.notes.size)

        // Con filtro: solo importantes
        viewModel.onImportantOnlyToggled()
        state = viewModel.uiState.value
        assertEquals(1, state.notes.size)
        assertTrue(state.notes.first().important)
    }

    @Test
    fun `al guardar notas el resumen se actualiza en el estado`() = runTest {
        // given
        val viewModel = NotesViewModel(FakeNotesRepository())

        // when: guardamos una nota normal y una importante
        viewModel.onTitleChanged("Normal")
        viewModel.onImportantChanged(false)
        viewModel.saveNote()

        viewModel.onTitleChanged("Importante")
        viewModel.onImportantChanged(true)
        viewModel.saveNote()

        // then: el resumen refleja las dos notas (caso de uso integrado en el ViewModel)
        val state = viewModel.uiState.value
        assertEquals(2, state.notesSummary.total)
        assertEquals(1, state.notesSummary.important)
        assertEquals(1, state.notesSummary.nonImportant)
        assertEquals(50, state.notesSummary.importantPercentage)
    }
}

