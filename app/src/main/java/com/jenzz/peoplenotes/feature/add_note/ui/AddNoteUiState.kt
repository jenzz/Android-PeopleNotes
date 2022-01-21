package com.jenzz.peoplenotes.feature.add_note.ui

import com.jenzz.peoplenotes.common.ui.TextFieldUiState

sealed class AddNoteUiState {

    object InitialLoad : AddNoteUiState()

    data class Loaded(
        val note: TextFieldUiState,
        val inputsEnabled: Boolean,
        val isNoteAdded: Boolean,
    ) : AddNoteUiState()
}
