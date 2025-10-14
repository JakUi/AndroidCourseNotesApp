package com.klyschenko.notes.domain

import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase {

    operator fun invoke(): Flow<List<Note>> {
        TODO()
    }
}

//fun main() {
//    val getAllNotesUseCase = GetAllNotesUseCase()
//    getAllNotesUseCase() // можем сделать так т.к. invoke это оператор
//}