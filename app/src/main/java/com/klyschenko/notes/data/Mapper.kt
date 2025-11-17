package com.klyschenko.notes.data

import com.klyschenko.notes.domain.ContentItem
import com.klyschenko.notes.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDBModel(): NoteDBModel {
    val contentAsString: String = Json.encodeToString(content.toContentItemDbModels())
    return NoteDBModel(id, title, contentAsString, updatedAt, isPinned)
}

fun List<ContentItem>.toContentItemDbModels(): List<ContentItemDbModel> {

    return map { contentItem ->
        when(contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel.Image(url = contentItem.url)
            }
            is ContentItem.Text -> {
                ContentItemDbModel.Text(content = contentItem.content)
            }
        }
    }
}

fun List<ContentItemDbModel>.toContentItems(): List<ContentItem> {

    return map { contentItem ->
        when(contentItem) {
            is ContentItemDbModel.Image -> {
                ContentItem.Image(url = contentItem.url)
            }
            is ContentItemDbModel.Text -> {
                ContentItem.Text(content = contentItem.content)
            }
        }
    }
}

fun NoteDBModel.toEntity(): Note {
    val contentItemDBModels = Json.decodeFromString<List<ContentItemDbModel>>(content)
    return Note(id, title, contentItemDBModels.toContentItems(), updatedAt, isPinned)
}

fun List<NoteDBModel>.toEntities(): List<Note> {
    return this.map{ it.toEntity() }
}