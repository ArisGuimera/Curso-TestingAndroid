package com.aristidevs.masterclass.notes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aristidevs.masterclass.notes.data.Note

const val TITLE_INPUT_TAG = "titleInput"
const val CONTENT_INPUT_TAG = "contentInput"
const val IMPORTANT_SWITCH_TAG = "importantSwitch"
const val SAVE_BUTTON_TAG = "saveButton"
const val IMPORTANT_ONLY_TOGGLE_TAG = "importantOnlyToggle"
const val NOTES_SUMMARY_TAG = "notesSummary"
const val NOTES_LIST_TAG = "notesList"
const val DELETE_BUTTON_TAG = "deleteButton"

@Composable
fun NotesScreen(
    viewModel: NotesViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Notas",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::onTitleChanged,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TITLE_INPUT_TAG),
            label = { Text("Título (mín. 3 caracteres)") },
            singleLine = true,
            isError = state.title.isNotBlank() && !state.canSave && !state.isLoading
        )

        OutlinedTextField(
            value = state.content,
            onValueChange = viewModel::onContentChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .testTag(CONTENT_INPUT_TAG),
            label = { Text("Contenido (opcional)") }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = state.important,
                onCheckedChange = viewModel::onImportantChanged,
                modifier = Modifier.testTag(IMPORTANT_SWITCH_TAG)
            )
            Text(text = "Importante")
        }

        Button(
            onClick = viewModel::saveNote,
            enabled = state.canSave && !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SAVE_BUTTON_TAG)
        ) {
            Text(if (state.isLoading) "Guardando..." else "Guardar")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Solo importantes")
            Checkbox(
                checked = state.importantOnly,
                onCheckedChange = { viewModel.onImportantOnlyToggled() },
                modifier = Modifier.testTag(IMPORTANT_ONLY_TOGGLE_TAG)
            )
        }

        HorizontalDivider()

        Text(
            text = "Resumen: ${state.notesSummary.total} notas (${state.notesSummary.important} importantes, ${state.notesSummary.importantPercentage}%)",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag(NOTES_SUMMARY_TAG)
        )

        Text(
            text = "Lista de notas",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag(NOTES_LIST_TAG),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.notes) { note ->
                NoteRow(note = note, onDelete = { viewModel.onDeleteNote(note) })
            }
        }
    }
}

@Composable
private fun NoteRow(note: Note, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (note.important) {
                Text(
                    text = "IMPORTANTE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag(DELETE_BUTTON_TAG)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar ${note.title}"
                )
            }
        }
        if (note.content.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
