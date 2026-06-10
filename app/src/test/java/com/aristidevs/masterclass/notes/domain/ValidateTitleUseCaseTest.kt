package com.aristidevs.masterclass.notes.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ValidateTitleUseCaseTest(
    private val title: String,
    private val expected: Boolean,
) {

    private val validateTitle = ValidateTitleUseCase()

    @Test
    fun validates_title() {
        assertEquals(expected, validateTitle(title))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "\"{0}\" -> {1}")
        fun data() = listOf(
            arrayOf("", false),
            arrayOf("   ", false),
            arrayOf("ab", false),
            arrayOf("a".repeat(41), false),
            arrayOf("123456", false),
            arrayOf("Nota@rara", false),
            arrayOf("Mi  nota", false),
            arrayOf("!?.,", false),
            arrayOf("Compra: pan", false),
            arrayOf("Mi nota", true),
            arrayOf("abc", true),
            arrayOf("  Mi nota válida  ", true),
            arrayOf("Título válido 123!?.,", true),
            arrayOf("Reunión 10am", true),
            arrayOf("a".repeat(40), true),
        )
    }
}
