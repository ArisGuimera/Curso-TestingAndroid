package com.aristidevs.masterclass.notes.domain

import com.aristidevs.masterclass.notes.data.Note
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class GetNotesSummaryUseCaseTest(
    private val important: Int,
    private val nonImportant: Int,
    private val expectedPercentage: Int,
) {

    private val getNotesSummary = GetNotesSummaryUseCase()

    @Test
    fun calculates_important_percentage() {
        val notes = buildList {
            repeat(important) { add(Note(title = "imp$it", content = "", important = true)) }
            repeat(nonImportant) { add(Note(title = "no$it", content = "", important = false)) }
        }

        assertEquals(expectedPercentage, getNotesSummary(notes).importantPercentage)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0} imp / {1} no -> {2}%")
        fun data() = listOf(
            arrayOf(0, 0, 0),
            arrayOf(2, 0, 100),
            arrayOf(0, 3, 0),
            arrayOf(2, 1, 67),  // bug (división entera) daría 66
            arrayOf(1, 2, 33),
            arrayOf(1, 5, 17),  // bug daría 16
            arrayOf(1, 0, 100),
            arrayOf(5, 1, 83),
        )
    }
}
