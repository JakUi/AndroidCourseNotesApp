package com.klyschenko.notes.data

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithContentDBModel(
    @Embedded // говорит что вместо того чтобы искать поле с названием noteDBModel
    // просто возьми все его поля и из них создай объект noteDBModel
    // без этой аннотации room будет искать столбец с названием noteDBModel
    val noteDBModel: NoteDBModel,
    @Relation(
        parentColumn = "id", // имя поля в родительской таблице
        entityColumn = "noteId" // имя поля в дочерней таблице
    )
    val content: List<ContentItemDbModel>
)
