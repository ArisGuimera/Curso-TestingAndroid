package com.aristidevs.masterclass.notes.domain

import com.aristidevs.masterclass.notes.data.Note
import org.junit.Assert.assertEquals
import org.junit.Test

class GetNotesSummaryUseCaseTest {

    private val getNotesSummary = GetNotesSummaryUseCase()

    @Test
    fun empty_list_returns_zero_summary() {
        // given
        val notes = emptyList<Note>()

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(0, summary.total)
        assertEquals(0, summary.important)
        assertEquals(0, summary.nonImportant)
        assertEquals(0, summary.importantPercentage)
    }

    @Test
    fun only_non_important_notes_have_zero_percent() {
        // given
        val notes = listOf(
            Note(id = 1, title = "A", content = "", important = false),
            Note(id = 2, title = "B", content = "", important = false),
        )

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(2, summary.total)
        assertEquals(0, summary.important)
        assertEquals(2, summary.nonImportant)
        assertEquals(0, summary.importantPercentage)
    }

    @Test
    fun only_important_notes_have_full_percent() {
        // given
        val notes = listOf(
            Note(id = 1, title = "A", content = "", important = true),
            Note(id = 2, title = "B", content = "", important = true),
        )

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(2, summary.total)
        assertEquals(2, summary.important)
        assertEquals(0, summary.nonImportant)
        assertEquals(100, summary.importantPercentage)
    }

    @Test
    fun mixed_notes_calculate_totals_and_percentage() {
        // given
        val notes = listOf(
            Note(id = 1, title = "A", content = "", important = true),
            Note(id = 2, title = "B", content = "", important = true),
            Note(id = 3, title = "C", content = "", important = false),
        )

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(3, summary.total)
        assertEquals(2, summary.important)
        assertEquals(1, summary.nonImportant)
        assertEquals(67, summary.importantPercentage)
    }

    @Test
    fun one_third_important_notes_rounds_to_33_percent() {
        // given
        val notes = listOf(
            Note(id = 1, title = "A", content = "", important = true),
            Note(id = 2, title = "B", content = "", important = false),
            Note(id = 3, title = "C", content = "", important = false),
        )

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(3, summary.total)
        assertEquals(1, summary.important)
        assertEquals(2, summary.nonImportant)
        assertEquals(33, summary.importantPercentage)
    }

    @Test
    fun single_important_note_gives_full_percent() {
        // given
        val notes = listOf(
            Note(id = 1, title = "Solo una", content = "x", important = true),
        )

        // when
        val summary = getNotesSummary(notes)

        // then
        assertEquals(1, summary.total)
        assertEquals(1, summary.important)
        assertEquals(0, summary.nonImportant)
        assertEquals(100, summary.importantPercentage)
    }
}
