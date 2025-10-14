package com.klyschenko.notes.domain

import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}

//fun main() {
//    val getAllNotesUseCase = GetAllNotesUseCase()
//    getAllNotesUseCase() // можем сделать так т.к. invoke это оператор
//}