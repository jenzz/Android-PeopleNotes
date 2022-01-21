package com.jenzz.peoplenotes.common.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.jenzz.peoplenotes.common.ui.TextFieldUiState

@Composable
fun NotesTextField(
    modifier: Modifier,
    enabled: Boolean,
    singleLine: Boolean,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    state: TextFieldUiState,
    onValueChange: (String) -> Unit,
) {
    val context = LocalContext.current
    Column {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            label = state.label?.let { label -> { Text(text = label.asString(context.resources)) } },
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
