package com.klyschenko.notes.presentation.screens.notes

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.nio.file.WatchEvent

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .padding(top = 48.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.pinnedNotes.forEach { note ->
                Text(
                    modifier = Modifier
                        .clickable {
                            viewModel.processCommand(NotesCommand.SwitchPinnedStatus(noteId = note.id))
                        },
                    text = "${note.title} - ${note.content}",
                    fontSize = 24.sp,
                )
            }
        }
        state.otherNotes.forEach { note ->
            Text(
                modifier = Modifier
                    .clickable {
                        viewModel.processCommand(NotesCommand.SwitchPinnedStatus(noteId = note.id))
                    },
                text = "${note.title} - ${note.content}",
                fontSize = 24.sp,
            )
        }
    }
}