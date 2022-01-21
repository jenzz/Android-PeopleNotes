package com.jenzz.peoplenotes.feature.add_note.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.TextFieldUiState
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.LoadingView
import com.jenzz.peoplenotes.common.ui.widgets.NotesTextField
import com.jenzz.peoplenotes.common.ui.widgets.SubmitButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

data class AddNoteScreenNavArgs(
    val personId: PersonId,
    val noteId: NoteId?,
)

@Destination(navArgsDelegate = AddNoteScreenNavArgs::class)
@Composable
fun AddNoteScreen(
    viewModel: AddNoteViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    AddNoteContent(
        state = viewModel.state,
        onNoteChange = viewModel::onNoteChange,
        onAddNoteClick = viewModel::onAddNote,
        onNoteAdded = { navigator.popBackStack() },
    )
}

@Composable
fun AddNoteContent(
    state: AddNoteUiState,
    onNoteChange: (String) -> Unit,
    onAddNoteClick: () -> Unit,
    onNoteAdded: () -> Unit,
) {
    when (state) {
        is AddNoteUiState.InitialLoad ->
            LoadingView()
        is AddNoteUiState.Loaded ->
            AddNoteLoaded(
                state = state,
                onNoteChange = onNoteChange,
                onAddNoteClick = onAddNoteClick,
                onNoteAdded = onNoteAdded,
            )
    }
}

@Composable
fun AddNoteLoaded(
    state: AddNoteUiState.Loaded,
    onNoteChange: (String) -> Unit,
    onAddNoteClick: () -> Unit,
    onNoteAdded: () -> Unit,
) {
    val currentOnNoteAdded by rememberUpdatedState(onNoteAdded)
    LaunchedEffect(state) {
        if (state.isNoteAdded) {
            currentOnNoteAdded()
        }
    }

    Column(
        modifier = Modifier.padding(all = MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.medium),
    ) {
        NotesInputField(
            enabled = state.inputsEnabled,
            imeAction = ImeAction.Done,
            state = state.note,
            onValueChange = onNoteChange,
        )
        SubmitButton(
            enabled = state.inputsEnabled,
            onClick = onAddNoteClick,
        )
    }
}

@Composable
private fun NotesInputField(
    enabled: Boolean,
    imeAction: ImeAction,
    state: TextFieldUiState,
    onValueChange: (String) -> Unit,
) {
    NotesTextField(
        modifier = Modifier.heightIn(min = 112.dp),
        enabled = enabled,
        singleLine = false,
        imeAction = imeAction,
        state = state,
        onValueChange = onValueChange,
    )
}
