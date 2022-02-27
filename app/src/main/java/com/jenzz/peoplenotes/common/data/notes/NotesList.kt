package com.jenzz.peoplenotes.common.data.notes

data class NotesList(
    val items: List<Note> = emptyList(),
    val totalCount: Int = 0,
) {

    val size: Int = items.size

    val isEmpty: Boolean = items.isEmpty()
}
