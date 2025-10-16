@file:OptIn(ExperimentalCoroutinesApi::class)

package com.klyschenko.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import com.klyschenko.notes.data.TestNotesRepositoryImpl
import com.klyschenko.notes.domain.AddNoteUseCase
import com.klyschenko.notes.domain.DeleteNoteUseCase
import com.klyschenko.notes.domain.EditNoteUseCase
import com.klyschenko.notes.domain.GetAllNotesUseCase
import com.klyschenko.notes.domain.GetNoteUseCase
import com.klyschenko.notes.domain.Note
import com.klyschenko.notes.domain.SearchNotesUseCase
import com.klyschenko.notes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class NotesViewModel: ViewModel() {

    private val repository = TestNotesRepositoryImpl

    private val addNoteUseCase = AddNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNoteUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        query
            .flatMapLatest {
                if (it.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNoteUseCase(it)
                }
            }
            .onEach {
                val pinnedNotes = it.filter { it.isPinned }
                val otherNotes = it.filter { !it.isPinned }
                _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
            }
            .launchIn(scope)
    }

    fun processCommand(command: NotesCommand) {
        when(command) {
            is NotesCommand.DeleteNote -> {
                deleteNoteUseCase(command.noteId)
            }
            is NotesCommand.EditNote -> {
                val title = command.note.title
                editNoteUseCase(command.note.copy(title = "$title edited"))
            }
            is NotesCommand.InputSearchQuery -> {

            }
            is NotesCommand.SwitchPinnedStatus -> {
                switchPinnedStatusUseCase(command.noteId)
            }
        }
    }
}

sealed interface NotesCommand {

    data class InputSearchQuery(val query: String): NotesCommand

    data class SwitchPinnedStatus(val noteId: Int): NotesCommand

    // Temp

    data class DeleteNote(val noteId: Int): NotesCommand

    data class EditNote(val note: Note): NotesCommand
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()

)