package com.jenzz.peoplenotes.feature.add_note.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.LoadingView
import com.jenzz.peoplenotes.common.ui.widgets.NotesInputField
import com.jenzz.peoplenotes.common.ui.widgets.SubmitButton
import com.jenzz.peoplenotes.ext.rememberFlowWithLifecycle
import com.jenzz.peoplenotes.feature.destinations.NotesScreenDestination
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
    navArgs: AddNoteScreenNavArgs,
) {
    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = viewModel.initialState)
    AddNoteContent(
        state = state,
        title = if (navArgs.noteId != null) R.string.edit_note else R.string.add_note,
        onNavigateUp = navigator::navigateUp,
        onNoteChange = viewModel::onNoteChange,
        onAddNoteClick = viewModel::onAddNote,
        onNoteAdded = {
            navigator.navigate(NotesScreenDestination(navArgs.personId)) {
                popUpTo(NotesScreenDestination.route) {
                    inclusive = true
                }
            }
        },
    )
}

@Composable
fun AddNoteContent(
    state: AddNoteUiState,
    @StringRes title: Int,
    onNavigateUp: () -> Unit,
    onNoteChange: (String) -> Unit,
    onAddNoteClick: () -> Unit,
    onNoteAdded: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                title = {
                    Text(text = stringResource(id = title))
                }
            )
        }
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
        val noteInput = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val onSubmit = {
            focusManager.clearFocus()
            onAddNoteClick()
        }
        NotesInputField(
            modifier = Modifier.focusRequester(noteInput),
            enabled = state.inputsEnabled,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            state = state.note,
            onValueChange = onNoteChange,
        )
        SubmitButton(
            enabled = state.inputsEnabled,
            text = R.string.save,
            onClick = onSubmit,
        )
        LaunchedEffect(Unit) {
            noteInput.requestFocus()
        }
    }
}
