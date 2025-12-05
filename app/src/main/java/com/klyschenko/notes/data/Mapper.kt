package com.klyschenko.notes.data

import com.klyschenko.notes.domain.ContentItem
import com.klyschenko.notes.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDBModel(): NoteDBModel {
    return NoteDBModel(id, title, updatedAt, isPinned)
}

fun List<ContentItem>.toContentItemDbModels(noteId: Int): List<ContentItemDbModel> {

    return mapIndexed { index, contentItem ->
        when(contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.IMAGE,
                    content = contentItem.url,
                    order = index
                )
            }
            is ContentItem.Text -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.TEXT,
                    content = contentItem.content,
                    order = index
                )
            }
        }
    }
}

fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {

    return map { contentItem ->
        when(contentItem.contentType) {
            ContentType.TEXT -> {
                ContentItem.Text(content = contentItem.content)
            }
            ContentType.IMAGE -> {
                ContentItem.Image(url = contentItem.content)
            }
        }
    }
}

fun NoteWithContentDBModel.toEntity(): Note {
    return Note(
        id = noteDBModel.id,
        title = noteDBModel.title,
        content = content.toContentItems(),
        updatedAt = noteDBModel.updatedAt,
        isPinned = noteDBModel.isPinned
    )
}

fun List<NoteWithContentDBModel>.toEntities(): List<Note> {
    return this.map{ it.toEntity() }
}