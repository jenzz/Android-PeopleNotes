package com.jenzz.peoplenotes.feature.add_person.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.TextFieldUiState
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.NotesTextField
import com.jenzz.peoplenotes.common.ui.widgets.SubmitButton
import com.jenzz.peoplenotes.ext.rememberFlowWithLifecycle
import com.jenzz.peoplenotes.feature.destinations.PeopleScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AddPersonScreen(
    viewModel: AddPersonViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = viewModel.initialState)
    AddPersonContent(
        state = state,
        onNavigateUp = navigator::navigateUp,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onNoteChange = viewModel::onNoteChange,
        onAddPersonClick = viewModel::onAddPerson,
        onPersonAdded = {
            navigator.navigate(PeopleScreenDestination) {
                popUpTo(PeopleScreenDestination.route) {
                    inclusive = true
                }
            }
        },
    )
}

@Composable
private fun AddPersonContent(
    state: AddPersonUiState,
    onNavigateUp: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onAddPersonClick: () -> Unit,
    onPersonAdded: () -> Unit,
) {
    val currentOnPersonAdded by rememberUpdatedState(onPersonAdded)
    LaunchedEffect(state) {
        if (state.isUserAdded) {
            currentOnPersonAdded()
        }
    }

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
                    Text(text = stringResource(id = R.string.add_person_manually))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(all = MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.medium),
        ) {
            // TODO JD Find a way to save & restore focus state across process death.
            val (firstNameInput, lastNameInput, notesInput) =
                remember { FocusRequester.createRefs() }
            val focusManager = LocalFocusManager.current
            val onSubmit = {
                focusManager.clearFocus()
                onAddPersonClick()
            }
            FirstNameInputField(
                modifier = Modifier.focusRequester(firstNameInput),
                enabled = state.inputsEnabled,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { lastNameInput.requestFocus() }),
                state = state.firstName,
                onValueChange = onFirstNameChange,
            )
            LastNameInputField(
                modifier = Modifier.focusRequester(lastNameInput),
                enabled = state.inputsEnabled,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { notesInput.requestFocus() }),
                state = state.lastName,
                onValueChange = onLastNameChange,
            )
            NotesInputField(
                modifier = Modifier.focusRequester(notesInput),
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
                firstNameInput.requestFocus()
            }
        }
    }
}

@Composable
private fun FirstNameInputField(
    modifier: Modifier,
    enabled: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    state: TextFieldUiState,
    onValueChange: (String) -> Unit,
) {
    NotesTextField(
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        state = state,
        onValueChange = onValueChange,
    )
}

@Composable
private fun LastNameInputField(
    modifier: Modifier,
    enabled: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    state: TextFieldUiState,
    onValueChange: (String) -> Unit,
) {
    NotesTextField(
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        state = state,
        onValueChange = onValueChange,
    )
}

@Composable
private fun NotesInputField(
    modifier: Modifier,
    enabled: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    state: TextFieldUiState,
    onValueChange: (String) -> Unit,
) {
    NotesTextField(
        modifier = modifier.heightIn(min = 112.dp),
        enabled = enabled,
        singleLine = false,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        state = state,
        onValueChange = onValueChange,
    )
}

@Preview(
    name = "Light Mode",
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark Mode",
    uiMode = UI_MODE_NIGHT_YES,
)
@Composable
private fun AddPersonContentPreview(
    @PreviewParameter(AddPersonPreviewParameterProvider::class)
    state: AddPersonUiState,
) {
    PeopleNotesTheme {
        Surface {
            AddPersonContent(
                state = state,
                onNavigateUp = {},
                onFirstNameChange = {},
                onLastNameChange = {},
                onNoteChange = {},
                onAddPersonClick = {},
                onPersonAdded = {},
            )
        }
    }
}

class AddPersonPreviewParameterProvider : CollectionPreviewParameterProvider<AddPersonUiState>(
    listOf(
        AddPersonUiState(
            firstName = TextFieldUiState("First Name"),
            lastName = TextFieldUiState("Last Name"),
            note = TextFieldUiState("Notes..."),
            inputsEnabled = true,
            isUserAdded = false,
        ),
        AddPersonUiState(
            firstName = TextFieldUiState("First Name"),
            lastName = TextFieldUiState("Last Name"),
            note = TextFieldUiState("Notes..."),
            inputsEnabled = false,
            isUserAdded = false,
        ),
        AddPersonUiState(
            firstName = TextFieldUiState(
                value = "First Name",
                error = R.string.first_name_cannot_be_empty
            ),
            lastName = TextFieldUiState(
                value = "Last Name",
                error = R.string.last_name_cannot_be_empty
            ),
            note = TextFieldUiState("Notes..."),
            inputsEnabled = true,
            isUserAdded = false,
        ),
    )
)
