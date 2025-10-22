package com.klyschenko.notes.domain

class SwitchPinnedStatusUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(noteId: Int) {
        repository.switchedPinnedStatus(noteId)
    }
}