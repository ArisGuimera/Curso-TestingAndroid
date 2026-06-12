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
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NotesContentUITest {

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
                onSaveClick = onSaveClicked,
                onDeleteClicked = onDeleteClicked,
            )
        }
    }

    @Test
    fun givenNotesInTheState_whenRendering_thenTheyAreDisplayed() {
        // given
        val state = NotesUiState(
            notes = listOf(
                Note(id = 1, title = "Buy bread", content = "", important = false),
                Note(id = 2, title = "Meeting", content = "", important = true),
            )
        )

        // when
        setContent(state)

        // then
        composeRule.onNodeWithText("Buy bread").assertIsDisplayed()
        composeRule.onNodeWithText("Meeting").assertIsDisplayed()
        composeRule.onNodeWithText("IMPORTANTE").assertIsDisplayed()
    }

    @Test
    fun givenSummaryInTheState_whenRendering_thenItIsDisplayed() {
        // given
        val state = NotesUiState(
            notesSummary = NotesSummary(
                total = 3,
                important = 2,
                nonImportant = 1,
                importantPercentage = 67
            )
        )

        // when
        setContent(state)

        // then
        composeRule.onNodeWithText("Resumen: 3 notas (2 importantes, 67%)").assertIsDisplayed()
    }

    @Test
    fun givenNote_whenClickingDelete_thenItInvokesOnDeleteClickedWithTheNote() {
        // given
        val note = Note(id = 1, title = "To delete", content = "", important = false)
        var deleted: Note? = null
        setContent(
            NotesUiState(notes = listOf(note)),
            onDeleteClicked = { deleted = it })

        // when
        composeRule.onNodeWithContentDescription("Borrar To delete").performClick()

        // then
        assertEquals(note, deleted)
    }

    @Test
    fun givenTitleField_whenTyping_thenItInvokesOnTitleChanged() {
        // given
        var typed = ""
        setContent(NotesUiState(), onTitleChanged = { typed = it })

        // when
        composeRule.onNodeWithTag(TITLE_INPUT).performTextInput("My note")

        // then
        assertEquals("My note", typed)
    }

    //
    @Test
    fun givenCanSaveIsTrue_whenClickingSave_thenItInvokesOnSaveClicked() {
        // given
        var saved = false
        setContent(NotesUiState(canSave = true), onSaveClicked = { saved = true })

        // when
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()

        // then
        assertTrue(saved)
    }

    @Test
    fun givenFilter_whenToggling_thenItInvokesOnImportantOnlyToggled() {
        // given
        var toggled = false
        setContent(NotesUiState(), onImportantOnlyToggled = { toggled = true })

        // when
        composeRule.onNodeWithTag(IMPORTANT_ONLY_TOGGLE_TAG).performClick()

        // then
        assertTrue(toggled)
    }
}
