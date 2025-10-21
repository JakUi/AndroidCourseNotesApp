package com.klyschenko.notes.presentation.screens.notes

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klyschenko.notes.domain.Note
import com.klyschenko.notes.presentation.ui.theme.Green
import com.klyschenko.notes.presentation.ui.theme.OtherNotesColors
import com.klyschenko.notes.presentation.ui.theme.PinnedNotesColors
import com.klyschenko.notes.presentation.ui.theme.Yellow200

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(top = 48.dp),
    ) {
        item {
            Title(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "All Notes"
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            SearchBar(
                modifier = Modifier.padding(horizontal = 24.dp),
                query = state.query,
                onQueryChange = {
                    viewModel.processCommand(NotesCommand.InputSearchQuery(it))
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            SubTitle(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Pinned"
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                itemsIndexed(
                    items = state.pinnedNotes,
                    key = {_, note -> note.id }
                ) {index, note ->
                    NoteCard(
                        note = note,
                        onNoteClick = {
                            viewModel.processCommand(NotesCommand.EditNote(it))
                        },
                        onDoubleClick = {
                            viewModel.processCommand(NotesCommand.DeleteNote(it.id))
                        },
                        onLongClick = {
                            viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id))
                        },
                        backgroundColor = PinnedNotesColors[index % PinnedNotesColors.size]
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            SubTitle(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Others"
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // вариант 1 (через item)
//        state.otherNotes.forEach {note ->
//            item(
//                key = note.id,
//            ) {
//                NotesCard(
//                    note = note,
//                    onNoteClick = {
//                        viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id))
//                    }
//                )
//            }
//        }

        // вариант 2 (через items)
        itemsIndexed(
            items = state.otherNotes,
            key = {_, note -> note.id }
        ) {index, note ->
            NoteCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                note = note,
                onNoteClick = {
                    viewModel.processCommand(NotesCommand.EditNote(it))
                },
                onDoubleClick = {
                    viewModel.processCommand(NotesCommand.DeleteNote(it.id))
                },
                onLongClick = {
                    viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id))
                },
                backgroundColor = OtherNotesColors[index % OtherNotesColors.size]
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun Title(
    modifier: Modifier = Modifier,
    text: String,

    ) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        leadingIcon = { // это иконка которая будет слева
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Notes",
                tint = MaterialTheme.colorScheme.surface
            )
        },
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun SubTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
    onDoubleClick: (Note) -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = {
                    onNoteClick(note)
                },
                onLongClick = {
                    onLongClick(note)
                },
                onDoubleClick = {
                    onDoubleClick(note)
                }
            )
            .padding(16.dp)
    ) {
        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = note.updatedAt.toString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = note.content,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}