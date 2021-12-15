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
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.feature.add_person.data.AddPersonViewModel

@Composable
fun AddPersonScreen(
    viewModel: AddPersonViewModel = hiltViewModel(),
    onPersonAdded: () -> Unit
) {
    AddPersonContent(
        state = viewModel.state,
        onFirstNameChanged = viewModel::onFirstNameChanged,
        onLastNameChanged = viewModel::onLastNameChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onAddPersonClick = viewModel::onAddPerson,
        onPersonAdded = onPersonAdded,
    )
}

@Composable
private fun AddPersonContent(
    state: AddPersonUiState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
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
            modifier = Modifier.padding(8.dp),
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
                value = state.firstName,
                onValueChanged = onFirstNameChanged,
            )
            LastNameInputField(
                modifier = Modifier.focusRequester(lastNameInput),
                enabled = state.inputsEnabled,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { notesInput.requestFocus() }),
                value = state.lastName,
                onValueChanged = onLastNameChanged,
            )
            NotesInputField(
                modifier = Modifier.focusRequester(notesInput),
                enabled = state.inputsEnabled,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                value = state.notes,
                onValueChanged = onNotesChanged,
            )
            SubmitButton(
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
    value: String,
    onValueChanged: (String) -> Unit,
) {
    PersonInputField(
        modifier = modifier,
        placeholder = R.string.first_name,
        enabled = enabled,
        singleLine = true,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        value = value,
        onValueChanged = onValueChanged,
    )
}

@Composable
private fun LastNameInputField(
    modifier: Modifier,
    enabled: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    value: String,
    onValueChanged: (String) -> Unit,
) {
    PersonInputField(
        modifier = modifier,
        placeholder = R.string.last_name,
        enabled = enabled,
        singleLine = true,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        value = value,
        onValueChanged = onValueChanged,
    )
}

@Composable
private fun NotesInputField(
    modifier: Modifier,
    enabled: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    value: String,
    onValueChanged: (String) -> Unit,
) {
    PersonInputField(
        modifier = modifier.heightIn(min = 112.dp),
        placeholder = R.string.notes_optional,
        enabled = enabled,
        singleLine = false,
        imeAction = imeAction,
        keyboardActions = keyboardActions,
        value = value,
        onValueChanged = onValueChanged,
    )
}

@Composable
private fun PersonInputField(
    modifier: Modifier,
    @StringRes placeholder: Int,
    enabled: Boolean,
    singleLine: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    value: String,
    onValueChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = singleLine,
        placeholder = { Text(text = stringResource(id = placeholder)) },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = keyboardActions,
        value = value,
        onValueChange = onValueChanged,
    )
}

@Composable
private fun SubmitButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
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
fun AddPersonContentPreview(
    @PreviewParameter(AddPersonPreviewParameterProvider::class)
    state: AddPersonUiState
) {
    PeopleNotesTheme {
        Surface {
            AddPersonContent(
                state = state,
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onNotesChanged = {},
                onAddPersonClick = {},
                onPersonAdded = {},
            )
        }
    }
}

class AddPersonPreviewParameterProvider : CollectionPreviewParameterProvider<AddPersonUiState>(
    listOf(
        AddPersonUiState(
            firstName = "First Name",
            lastName = "Last Name",
            notes = "Notes...",
            inputsEnabled = true,
            isUserAdded = false,
        ),
        AddPersonUiState(
            firstName = "First Name",
            lastName = "Last Name",
            notes = "Notes...",
            inputsEnabled = false,
            isUserAdded = false,
        ),
    )
)
