package com.jenzz.peoplenotes.common.data.notes

data class NotesList(
    val items: List<Note>,
    val totalCount: Int,
) {

    val isEmpty: Boolean = items.isEmpty()
}
