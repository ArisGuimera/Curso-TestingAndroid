package com.aristidevs.masterclass.notes.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
        composeRule.onNodeWithTag(TITLE_INPUT_TAG)
            .performTextInput("Nota UI")

        composeRule.onNodeWithTag(SAVE_BUTTON_TAG)
            .performClick()

        composeRule.onNodeWithText("Nota UI")
            .assertIsDisplayed()
    }
    @Test
    fun save_note_clears_the_form_after_saving() {
        composeRule.onNodeWithTag(TITLE_INPUT_TAG)
            .performTextInput("Nota UI")
        composeRule.onNodeWithTag(CONTENT_INPUT_TAG)
            .performTextInput("Descripción")

        composeRule.onNodeWithTag(SAVE_BUTTON_TAG)
            .performClick()

        // La nota se guarda y aparece en la lista...
        composeRule.onNodeWithText("Nota UI")
            .assertIsDisplayed()

        // ...y el formulario se limpia: con el título vacío, Guardar se deshabilita.
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG)
            .assertIsNotEnabled()
    }

    @Test
    fun important_only_filter_hides_and_shows_items() {
        // Crear nota no importante
        composeRule.onNodeWithTag(TITLE_INPUT_TAG)
            .performTextInput("Normal UI")
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG)
            .performClick()

        // Crear nota importante
        composeRule.onNodeWithTag(TITLE_INPUT_TAG)
            .performTextInput("Importante UI")
        composeRule.onNodeWithTag(IMPORTANT_SWITCH_TAG)
            .performClick()
        composeRule.onNodeWithTag(SAVE_BUTTON_TAG)
            .performClick()

        // Sin filtro: deberían aparecer las dos
        composeRule.onNodeWithText("Normal UI").assertIsDisplayed()
        composeRule.onNodeWithText("Importante UI").assertIsDisplayed()

        // Activar filtro "solo importantes"
        composeRule.onNodeWithTag(IMPORTANT_ONLY_TOGGLE_TAG)
            .performClick()

        composeRule.onNodeWithText("Importante UI").assertIsDisplayed()
        // La nota normal ya no debería estar visible
        composeRule.onNodeWithText("Normal UI").assertDoesNotExist()
    }
}

