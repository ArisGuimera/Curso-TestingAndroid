package com.aristidevs.masterclass.notes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aristidevs.masterclass.notes.data.Note
import com.aristidevs.masterclass.notes.domain.GetNotesSummaryUseCase
import com.aristidevs.masterclass.notes.domain.NotesRepository
import com.aristidevs.masterclass.notes.domain.NotesSummary
import com.aristidevs.masterclass.notes.domain.ValidateTitleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotesUiState(
    val title: String = "",
    val content: String = "",
    val important: Boolean = false,
    val importantOnly: Boolean = false,
    val notes: List<Note> = emptyList(),
    val notesSummary: NotesSummary = NotesSummary(0, 0, 0, 0),
    val canSave: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NotesViewModel(
    private val repository: NotesRepository,
    private val getNotesSummary: GetNotesSummaryUseCase = GetNotesSummaryUseCase(),
    private val validateTitle: ValidateTitleUseCase = ValidateTitleUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NotesUiState(notesSummary = getNotesSummary(emptyList()))
    )
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var allNotes: List<Note> = emptyList()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            repository.getNotes().collect { notes ->
                allNotes = notes
                _uiState.update { current ->
                    current.copy(
                        notes = applyFilter(
                            notes = notes,
                            importantOnly = current.importantOnly
                        ),
                        notesSummary = getNotesSummary(notes)
                    )
                }
            }
        }
    }

    fun onTitleChanged(newTitle: String) {
        _uiState.update { current ->
            current.copy(
                title = newTitle,
                canSave = !current.isLoading && validateTitle(newTitle),
                errorMessage = null
            )
        }
    }

    fun onContentChanged(newContent: String) {
        _uiState.update { current ->
            current.copy(
                content = newContent,
                errorMessage = null
            )
        }
    }

    fun onImportantChanged(newImportant: Boolean) {
        _uiState.update { current ->
            current.copy(
                important = newImportant,
                errorMessage = null
            )
        }
    }

    fun onImportantOnlyToggled() {
        _uiState.update { current ->
            val newImportantOnly = !current.importantOnly
            current.copy(
                importantOnly = newImportantOnly,
                notes = applyFilter(
                    notes = allNotes,
                    importantOnly = newImportantOnly
                ),
                notesSummary = getNotesSummary(allNotes)
            )
        }
    }

    fun saveNote() {
        val current = _uiState.value
        val normalizedTitle = validateTitle.normalize(current.title)
        if (!validateTitle(current.title) || current.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, canSave = false, errorMessage = null) }
            try {
                repository.addNote(
                    title = normalizedTitle,
                    content = current.content,
                    important = current.important
                )
                _uiState.update { it.copy(title = "", content = "", important = false) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(errorMessage = t.message) }
            } finally {
                _uiState.update { latest ->
                    latest.copy(
                        isLoading = false,
                        canSave = validateTitle(latest.title)
                    )
                }
            }
        }
    }

    fun onDeleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    private fun applyFilter(
        notes: List<Note>,
        importantOnly: Boolean
    ): List<Note> =
        if (importantOnly) notes.filter { it.important } else notes
}

