package com.klyschenko.notes.data

import android.content.Context
import com.klyschenko.notes.domain.Note
import com.klyschenko.notes.domain.NotesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : NotesRepository {
    private val notesDatabase = NotesDatabase.getInstance(context)
    private val notesDao = notesDatabase.notesDao()

    override suspend fun addNote(
        title: String,
        content: String,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val noteDBModel = NoteDBModel(0, title, content, updatedAt, isPinned)
        notesDao.addNote(noteDBModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        notesDao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        notesDao.addNote(note.toDBModel())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() }
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchedPinnedStatus(noteId: Int) {
        notesDao.switchedPinnedStatus(noteId)
    }

    companion object {

        private val LOCK = Any()
        private var instance: NotesRepositoryImpl? = null

        fun getInstance(context: Context): NotesRepositoryImpl {

            instance?.let { return it }

            synchronized(LOCK) {
                instance?.let { return it }
                return NotesRepositoryImpl(context).also {
                    instance = it
                }
            }
        }
    }
}