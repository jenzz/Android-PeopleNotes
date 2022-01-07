package com.jenzz.peoplenotes.feature.add_person.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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

@Composable
fun AddPersonScreen(
    viewModel: AddPersonViewModel = hiltViewModel(),
    onPersonAdded: () -> Unit,
) {
    AddPersonContent(
        state = viewModel.state,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onNoteChange = viewModel::onNoteChange,
        onAddPersonClick = viewModel::onAddPerson,
        onPersonAdded = onPersonAdded,
    )
}

@Composable
private fun AddPersonContent(
    state: AddPersonUiState,
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
                title = {
                    Text(text = stringResource(id = R.string.add_person_manually))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val (firstNameInput, lastNameInput, notesInput) = remember { FocusRequester.createRefs() }
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
                state = TextFieldUiState(state.note),
                onValueChange = onNoteChange,
            )
            SubmitButton(
                modifier = Modifier.padding(top = 8.dp),
                enabled = state.inputsEnabled,
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
    PersonInputField(
        modifier = modifier,
        label = R.string.first_name,
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
    PersonInputField(
        modifier = modifier,
        label = R.string.last_name,
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
    PersonInputField(
        modifier = modifier.heightIn(min = 112.dp),
        label = R.string.notes_optional,
        enabled = enabled,
        singleLine = false,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        state = state,
        onValueChange = onValueChange,
    )
}

@Composable
private fun PersonInputField(
    modifier: Modifier,
    @StringRes label: Int,
    enabled: Boolean,
    singleLine: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    state: TextFieldUiState,
    onValueChange: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            label = { Text(text = stringResource(id = label)) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
            keyboardActions = keyboardActions,
            value = state.value,
            isError = state.isError,
            onValueChange = onValueChange,
        )
        if (state.isError) {
            Text(
                text = stringResource(id = state.requireError()),
                color = MaterialTheme.colors.error,
            )
        }
    }
}

@Composable
private fun SubmitButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        Text(text = stringResource(id = R.string.add))
    }
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
            note = "Notes...",
            inputsEnabled = true,
            isUserAdded = false,
        ),
        AddPersonUiState(
            firstName = TextFieldUiState("First Name"),
            lastName = TextFieldUiState("Last Name"),
            note = "Notes...",
            inputsEnabled = false,
            isUserAdded = false,
        ),
        AddPersonUiState(
            firstName = TextFieldUiState("First Name", R.string.first_name_cannot_be_empty),
            lastName = TextFieldUiState("Last Name", R.string.last_name_cannot_be_empty),
            note = "Notes...",
            inputsEnabled = true,
            isUserAdded = false,
        ),
    )
)
