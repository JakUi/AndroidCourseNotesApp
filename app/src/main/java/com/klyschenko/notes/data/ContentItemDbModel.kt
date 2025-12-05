package com.klyschenko.notes.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "content",
    primaryKeys = ["noteId", "order"],
    foreignKeys = [
        ForeignKey(
            entity = NoteDBModel::class, // в какой таблице смотреть
            parentColumns = ["id"], // по какому ключу смотреть
            childColumns = ["noteId"], // по какому из свойств определять к какой заметке относится этот элемент
            onDelete = ForeignKey.CASCADE // стратегия поведения после удаления заметки CASCADE - удалить все элементы
        )
    ]
)
data class ContentItemDbModel(
    val noteId: Int,
    val contentType: ContentType,
    val content: String,
    val order: Int
) {

}

enum class ContentType{

    TEXT, IMAGE
}