package com.klyschenko.notes.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteDBModel::class], // аналог записи arrayOf(NoteDBModel::class)
    version = 2,
    exportSchema = false
)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun notesDao(): NotesDao
}