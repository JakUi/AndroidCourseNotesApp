package com.klyschenko.notes.domain

class GetNoteUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(noteId: Int): Note {
        return repository.getNote(noteId = noteId)
    }
}