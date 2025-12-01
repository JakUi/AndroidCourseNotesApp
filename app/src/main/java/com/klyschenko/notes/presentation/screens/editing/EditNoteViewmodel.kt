package com.klyschenko.notes.presentation.screens.editing

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klyschenko.notes.domain.ContentItem
import com.klyschenko.notes.domain.ContentItem.Image
import com.klyschenko.notes.domain.ContentItem.Text
import com.klyschenko.notes.domain.DeleteNoteUseCase
import com.klyschenko.notes.domain.EditNoteUseCase
import com.klyschenko.notes.domain.GetNoteUseCase
import com.klyschenko.notes.domain.Note
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewmodel.Factory::class)
class EditNoteViewmodel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: Int,
    private val editNoteUseCase: EditNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {


    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId)
                EditNoteState.Editing(note)
            }
        }
    }

    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Back -> {
                _state.update { EditNoteState.Finished }
            }

            is EditNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newContent = ContentItem.Text(content = command.content)
                        val newNote = previousState.note.copy(content = listOf(newContent))
                        previousState.copy(note = newNote)
                    } else {
                        previousState
                    }
                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newNote = previousState.note.copy(title = command.title)
                        previousState.copy(note = newNote)
                    } else {
                        previousState
                    }
                }
            }

            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            editNoteUseCase(note)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            deleteNoteUseCase(note.id)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            is EditNoteCommand.AddImage -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        previousState.note.content.toMutableList().apply {
                            val lastItem = last()
                            if (lastItem is ContentItem.Text && lastItem.content.isBlank()) {
                                removeAt(lastIndex)
                            }
                            add(Image(command.uri.toString()))
                            add(Text(""))
                        }.let {
                            val newNote = previousState.note.copy(content = it)
                            val last = previousState.copy(note = newNote)
                            last
                        }
                    } else {
                        previousState
                    }
                }
            }

            is EditNoteCommand.DeleteImage -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        previousState.note.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            val newNote = previousState.note.copy(content = it)
                            val last = previousState.copy(note = newNote)
                            last
                        }
                    } else {
                        previousState
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("noteId") noteId: Int
        ): EditNoteViewmodel
    }

    sealed interface EditNoteCommand {

        data class InputTitle(val title: String) : EditNoteCommand

        data class InputContent(val content: String, val index: Int) : EditNoteCommand

        data class AddImage(val uri: Uri) : EditNoteCommand

        data class DeleteImage(val index: Int) : EditNoteCommand

        data object Save : EditNoteCommand

        data object Back : EditNoteCommand

        data object Delete : EditNoteCommand
    }

    sealed interface EditNoteState {

        data object Initial : EditNoteState

        data class Editing(
            val note: Note
        ) : EditNoteState {

            val isSaveEnabled: Boolean
                get() {
                    return when {
                        note.title.isBlank() -> false
                        note.content.isEmpty() -> false
                        else -> {
                            note.content.any {
                                it !is ContentItem.Text || it.content.isNotBlank()
                            }
                        }
                    }
                }
        }

        data object Finished : EditNoteState
    }
}