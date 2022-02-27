package com.jenzz.peoplenotes.common.ui.widgets

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jenzz.peoplenotes.common.ui.TextFieldUiState

@Composable
fun NotesInputField(
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
