package com.aristidevs.masterclass.notes.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateTitleUseCaseTest {

    private val validateTitle = ValidateTitleUseCase()

    @Test
    fun empty_string_is_invalid() {
        assertFalse(validateTitle(""))
    }

    @Test
    fun only_spaces_is_invalid() {
        assertFalse(validateTitle("   "))
    }

    @Test
    fun title_with_less_than_three_chars_is_invalid() {
        assertFalse(validateTitle("ab"))
    }

    @Test
    fun title_with_more_than_max_length_is_invalid() {
        val longTitle = "a".repeat(41)
        assertFalse(validateTitle(longTitle))
    }

    @Test
    fun numeric_only_title_is_invalid() {
        assertFalse(validateTitle("123456"))
    }

    @Test
    fun title_with_not_allowed_symbol_is_invalid() {
        assertFalse(validateTitle("Nota@rara"))
    }

    @Test
    fun title_with_double_spaces_is_invalid() {
        assertFalse(validateTitle("Mi  nota"))
    }

    @Test
    fun valid_trimmed_title_is_valid() {
        assertTrue(validateTitle("  Mi nota válida  "))
    }

    @Test
    fun title_with_allowed_punctuation_is_valid() {
        assertTrue(validateTitle("Título válido 123!?.,"))
    }
}
