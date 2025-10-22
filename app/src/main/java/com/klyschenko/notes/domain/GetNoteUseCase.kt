package com.klyschenko.notes.domain

class GetNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(noteId: Int): Note {
        return repository.getNote(noteId = noteId)
    }
}