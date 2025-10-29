package com.klyschenko.notes.data

import com.klyschenko.notes.domain.Note

fun Note.toDBModel(): NoteDBModel {
    return NoteDBModel(id, title, content, updatedAt, isPinned)
}

fun NoteDBModel.toEntity(): Note {
    return Note(id, title, content, updatedAt, isPinned)
}
