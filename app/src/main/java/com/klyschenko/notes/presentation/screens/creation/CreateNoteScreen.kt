@file:OptIn(ExperimentalMaterial3Api::class)

package com.klyschenko.notes.presentation.screens.creation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.klyschenko.notes.domain.ContentItem
import com.klyschenko.notes.presentation.ui.theme.CustomIcons
import com.klyschenko.notes.presentation.utils.DateFormatter

@Composable
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    viewmodel: CreateNoteViewmodel = hiltViewModel(),
    onFinished: () -> Unit
) {

    val state = viewmodel.state.collectAsState()
    val currentState = state.value
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), // contract для открытия галлереи
        onResult = { uri ->
            uri?.let {
                viewmodel.processCommand(
                    CreateNoteViewmodel.CreateNoteCommand.AddImage(uri)
                )
            }
        }
    )
    when (currentState) {
        is CreateNoteViewmodel.CreateNoteState.Creation -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Create Note",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable {
                                        viewmodel.processCommand(CreateNoteViewmodel.CreateNoteCommand.Back)
                                    },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        },
                        actions = {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 24.dp)
                                    .clickable {
                                        imagePicker.launch("image/*") // сюда передаются MIME-типы (можно погуглить)
                                    },
                                imageVector = CustomIcons.AddPhoto,
                                contentDescription = "Add photo from gallery",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = currentState.title,
                        onValueChange = {
                            viewmodel.processCommand(
                                CreateNoteViewmodel.CreateNoteCommand.InputTitle(
                                    it
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        placeholder = {
                            Text(
                                text = "Title",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        }
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = DateFormatter.formatCurrentDate(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        currentState.content.forEachIndexed { index, contentItem ->
                            item(key = index) {
                                when (contentItem) {
                                    is ContentItem.Image -> {
                                        TextContent(
                                            text = contentItem.url,
                                            onTextChanged = {}
                                        )
                                    }

                                    is ContentItem.Text -> {
                                        TextContent(
                                            text = contentItem.content,
                                            onTextChanged = {
                                                viewmodel.processCommand(
                                                    CreateNoteViewmodel.CreateNoteCommand.InputContent(
                                                        content = it,
                                                        index = index
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        onClick = {
                            viewmodel.processCommand(CreateNoteViewmodel.CreateNoteCommand.Save)
                        },
                        shape = RoundedCornerShape(10.dp),
                        enabled = currentState.isSaveEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.1f
                            ),
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Save Note",
                        )
                    }
                }
            }
        }

        CreateNoteViewmodel.CreateNoteState.Finished -> {
            LaunchedEffect(
                key1 = Unit
            ) {
                onFinished()
            }
        }
    }
}

@Composable
private fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        value = text,
        onValueChange = onTextChanged,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        placeholder = {
            Text(
                text = "Note something down",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    )
}