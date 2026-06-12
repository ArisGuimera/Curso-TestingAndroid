package com.aristidevs.masterclass.notes.domain

import com.aristidevs.masterclass.notes.data.Note
import org.junit.Assert.*
import org.junit.Test

class GetNotesSummaryUseCaseTest {

    private val getNotesSummary = GetNotesSummaryUseCase()


    @Test
    fun `given no notes when calculating the summary then the percentage is 0`() {
        // given
        val notes = emptyList<Note>()

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(0, summary.importantPercentage)
    }

    @Test
    fun `given only important notes when calculating the summary then the percentage is 100`() {
        // given
        val notes = notesWith(important = 2, normal = 0)

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(100, summary.importantPercentage)
    }

    @Test
    fun `given no important notes when calculating the summary then the percentage is 0`() {
        // given
        val notes = notesWith(important = 0, normal = 3)

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(0, summary.importantPercentage)
    }

    @Test
    fun `given 2 important out of 3 when calculating the summary then it rounds to 67`() {
        // given
        val notes = notesWith(important = 2, normal = 1)

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(67, summary.importantPercentage)
    }

    @Test
    fun `given 1 important out of 6 when calculating the summary then it rounds to 17`() {
        // given
        val notes = notesWith(important = 1, normal = 5)

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(17, summary.importantPercentage)
    }

    @Test
    fun `given a mix of notes when calculating the summary then it returns all the totals`() {
        // given
        val notes = notesWith(important = 2, normal = 1)

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(3, summary.total)
        assertEquals(2, summary.important)
        assertEquals(1, summary.nonImportant)
        assertEquals(67, summary.importantPercentage)
    }

    private fun notesWith(important: Int, normal: Int): List<Note> = buildList {
        repeat(important) { add(Note(title = "important$it", content = "", important = true)) }
        repeat(normal) { add(Note(title = "normal$it", content = "", important = false)) }
    }

}