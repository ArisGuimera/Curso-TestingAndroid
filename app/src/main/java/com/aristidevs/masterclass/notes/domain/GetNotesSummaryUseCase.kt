package com.aristidevs.masterclass.notes.domain

import com.aristidevs.masterclass.notes.data.Note

/**
 * Resumen de estadísticas de una lista de notas.
 * Se usa en la pantalla de notas para mostrar total, importantes y porcentaje.
 */
data class NotesSummary(
    val total: Int,
    val important: Int,
    val nonImportant: Int,
    val importantPercentage: Int
)

/**
 * Caso de uso: calcula el resumen de una lista de notas.
 * Lógica pura, usada por el ViewModel para exponer [NotesSummary] en el estado de la UI.
 */
class GetNotesSummaryUseCase {

    operator fun invoke(notes: List<Note>): NotesSummary {
        val total = notes.size
        val important = notes.count { it.important }
        val nonImportant = total - important

        val percentage = if (total == 0) {
            0
        } else {
            (important * 100) / total
        }

        return NotesSummary(
            total = total,
            important = important,
            nonImportant = nonImportant,
            importantPercentage = percentage
        )
    }
}
