package com.aristidevs.masterclass.notes.domain

/**
 * Caso de uso para validar el título de una nota.
 *
 * Reglas:
 * - Longitud entre 3 y 40 caracteres (después de trim).
 * - Debe contener al menos una letra.
 * - Solo permite letras, dígitos, espacios y . , ! ?
 * - No se permiten espacios dobles consecutivos.
 */
class ValidateTitleUseCase {

    fun normalize(title: String): String = title.trim()

    operator fun invoke(title: String): Boolean {
        val normalized = normalize(title)

        if (normalized.length !in 3..40) return false
        if (!normalized.any { it.isLetter() }) return false

        val allowedPunctuation = setOf('.', ',', '!', '?')
        if (normalized.any { ch ->
                !ch.isLetterOrDigit() &&
                        ch != ' ' &&
                        ch !in allowedPunctuation
            }
        ) {
            return false
        }

        if ("  " in normalized) return false

        return true
    }
}

