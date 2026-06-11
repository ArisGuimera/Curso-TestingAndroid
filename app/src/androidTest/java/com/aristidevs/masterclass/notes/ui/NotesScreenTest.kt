package com.aristidevs.masterclass.notes.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.aristidevs.masterclass.notes.data.Note
import com.aristidevs.masterclass.notes.domain.NotesSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(
        state: NotesUiState,
        onTitleChanged: (String) -> Unit = {},
        onImportantOnlyToggled: () -> Unit = {},
        onSaveClicked: () -> Unit = {},
        onDeleteClicked: (Note) -> Unit = {},
    ) {
        composeRule.setContent {
            NotesContent(
                state = state,
                onTitleChanged = onTitleChanged,
                onContentChanged = {},
                onImportantChanged = {},
                onImportantOnlyToggled = onImportantOnlyToggled,
                onSaveClicked = onSaveClicked,
                onDeleteClicked = onDeleteClicked,
            )
        }
    }

    @Test
    fun notes_are_displayed_in_the_list() {
        setContent(
            NotesUiState(
                notes = listOf(
                    Note(id = 1, title = "Comprar pan", content = "", important = false),
                    Note(id = 2, title = "Reunión", content = "", important = true),
                )
            )
        )

        composeRule.onNodeWithText("Comprar pan").assertIsDisplayed()
        composeRule.onNodeWithText("Reunión").assertIsDisplayed()
        composeRule.onNodeWithText("IMPORTANTE").assertIsDisplayed()
    }

    @Test
    fun summary_is_displayed() {
        setContent(
            NotesUiState(
                notesSummary = NotesSummary(total = 3, important = 2, nonImportant = 1, importantPercentage = 67)
            )
        )

        composeRule.onNodeWithText("Resumen: 3 notas (2 importantes, 67%)").assertIsDisplayed()
    }

    @Test
    fun typing_title_invokes_callback() {
        var typed = ""
        setContent(NotesUiState(), onTitleChanged = { typed = it })

        composeRule.onNodeWithTag(TITLE_INPUT_TAG).performTextInput("Mi nota")

        assertEquals("Mi nota", typed)
    }

    @Test
    fun clicking_save_invokes_callback() {
        var saved = false
        setContent(NotesUiState(canSave = true), onSaveClicked = { saved = true })

        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()

        assertTrue(saved)
    }

    @Test
    fun toggling_important_only_invokes_callback() {
        var toggled = false
        setContent(NotesUiState(), onImportantOnlyToggled = { toggled = true })

        composeRule.onNodeWithTag(IMPORTANT_ONLY_TOGGLE_TAG).performClick()

        assertTrue(toggled)
    }

    @Test
    fun clicking_delete_invokes_callback_with_note() {
        val note = Note(id = 1, title = "Para borrar", content = "", important = false)
        var deleted: Note? = null
        setContent(NotesUiState(notes = listOf(note)), onDeleteClicked = { deleted = it })

        composeRule.onNodeWithContentDescription("Borrar Para borrar").performClick()

        assertEquals(note, deleted)
    }
}
