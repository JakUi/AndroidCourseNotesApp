package com.klyschenko.notes.domain

class SwitchPinnedStatusUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(noteId: Int) {
        repository.switchedPinnedStatus(noteId)
    }
}