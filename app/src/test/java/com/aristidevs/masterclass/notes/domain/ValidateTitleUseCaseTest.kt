package com.aristidevs.masterclass.notes.domain

import org.junit.Assert.*
import org.junit.Test

class ValidateTitleUseCaseTest {

    private val validateTitle = ValidateTitleUseCase()

    @Test
    fun `given a normal title when validating then it is valid`() {
        // given
        val title = "My note"

        // when
        val result = validateTitle(title)

        // then
        assertTrue(result)
    }

    @Test
    fun `given an empty title when validating then it is invalid`() {
        // given
        val title = ""

        // when
        val result = validateTitle(title)

        // then
        assertFalse(result)
    }

    @Test
    fun `given a too short title when validating then it is invalid`() {
        // given
        val title = "ab"

        // when
        val result = validateTitle(title)

        // then
        assertFalse(result)
    }

    @Test
    fun `given a too long title when validating then it is invalid`() {
        // given
        val title = "a".repeat(41)

        // when
        val result = validateTitle(title)

        // then
        assertFalse(result)
    }

    @Test
    fun `given a numbers only title when validating then it is invalid`() {
        // given
        val title = "123456"

        // when
        val result = validateTitle(title)

        // then
        assertFalse(result)
    }

    @Test
    fun `given a title with invalid characters when validating then it is invalid`() {
        // given
        val title = "Weird@note"

        // when
        val result = validateTitle(title)

        // then
        assertFalse(result)
    }

    @Test
    fun `given a title with double spaces when validating then it is invalid`() {
        // given
        val title = "My  note"

        // when
        val result = validateTitle(title)

        // then
        assertFalse(result)
    }

    @Test
    fun `given a title with surrounding spaces when validating then it is trimmed and valid`() {
        // given
        val title = "  My note  "

        // when
        val result = validateTitle(title)

        // then
        assertTrue(result)
    }
}




