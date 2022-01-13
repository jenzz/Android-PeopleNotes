package com.jenzz.peoplenotes.common.data.notes

data class Notes(
    val notes: List<Note>,
    val totalCount: Int,
) {

    companion object {

        val DEFAULT = Notes(
            notes = emptyList(),
            totalCount = 0,
        )
    }

    val isEmpty: Boolean = notes.isEmpty()
}
