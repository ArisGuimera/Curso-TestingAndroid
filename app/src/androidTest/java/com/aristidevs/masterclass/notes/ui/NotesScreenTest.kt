package com.aristidevs.masterclass.notes.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aristidevs.masterclass.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun save_note_shows_item_in_list() {
        composeRule.onNodeWithTag(TITLE_INPUT_TAG).performTextInput("Nota UI")
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()

        composeRule.onNodeWithText("Nota UI").assertIsDisplayed()
    }

    @Test
    fun important_only_filter_hides_non_important() {
        // Nota normal
        composeRule.onNodeWithTag(TITLE_INPUT_TAG).performTextInput("Normal UI")
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()

        // Nota importante
        composeRule.onNodeWithTag(TITLE_INPUT_TAG).performTextInput("Importante UI")
        composeRule.onNodeWithTag(IMPORTANT_SWITCH_TAG).performClick()
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()

        // Activar el filtro "solo importantes"
        composeRule.onNodeWithTag(IMPORTANT_ONLY_TOGGLE_TAG).performClick()

        composeRule.onNodeWithText("Importante UI").assertIsDisplayed()
        composeRule.onNodeWithText("Normal UI").assertDoesNotExist()
    }

    @Test
    fun delete_note_removes_it_from_list() {
        composeRule.onNodeWithTag(TITLE_INPUT_TAG).performTextInput("Para borrar")
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
        composeRule.onNodeWithText("Para borrar").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Borrar Para borrar").performClick()

        composeRule.onNodeWithText("Para borrar").assertDoesNotExist()
    }
}
