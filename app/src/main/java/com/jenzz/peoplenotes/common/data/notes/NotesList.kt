package com.jenzz.peoplenotes.common.data.notes

data class NotesList(
    val items: List<Note>,
    val totalCount: Int,
) {

    companion object {

        val DEFAULT = NotesList(
            items = emptyList(),
            totalCount = 0,
        )
    }

    val isEmpty: Boolean = items.isEmpty()
}
