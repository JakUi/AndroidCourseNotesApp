package com.klyschenko.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteWithContentDBModel>>

    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId: Int): NoteWithContentDBModel

    @Query("""
        SELECT DISTINCT notes.* FROM notes JOIN content
        ON notes.id == content.noteId
        WHERE title LIKE '%' || :query || '%'
        OR content LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC""") // || в SQL означает конкатенацию
    fun searchNotes(query: String): Flow<List<NoteDBModel>>

    @Query("DELETE FROM notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId")
    suspend fun switchedPinnedStatus(noteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(noteDBModel: NoteDBModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNoteContent(content: List<ContentItemDbModel>)

    @Query("DELETE FROM content WHERE noteId == :noteId")
    suspend fun deleteNoteContent(noteId: Int)
}