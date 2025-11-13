package com.klyschenko.notes.di

import android.content.Context
import com.klyschenko.notes.data.NotesDatabase
import com.klyschenko.notes.data.NotesRepositoryImpl
import com.klyschenko.notes.domain.AddNoteUseCase
import com.klyschenko.notes.domain.DeleteNoteUseCase
import com.klyschenko.notes.domain.EditNoteUseCase
import com.klyschenko.notes.domain.GetAllNotesUseCase
import com.klyschenko.notes.domain.GetNoteUseCase
import com.klyschenko.notes.domain.SearchNotesUseCase
import com.klyschenko.notes.domain.SwitchPinnedStatusUseCase
import com.klyschenko.notes.presentation.MainActivity

class Component(
    context: Context
) {

//    val database = NotesDatabase.getInstance(context)

    val repository = NotesRepositoryImpl(context = context)

    val addNoteUseCase = AddNoteUseCase(repository)

    val deleteNoteUseCase = DeleteNoteUseCase(repository)

    val editNoteUseCase = EditNoteUseCase(repository)

    val getAllNoteUseCase = GetAllNotesUseCase(repository)

    val getNoteUseCase = GetNoteUseCase(repository)

    val searchNoteUseCase = SearchNotesUseCase(repository)

    val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    fun inject(mainActivity: MainActivity) {
    }

}