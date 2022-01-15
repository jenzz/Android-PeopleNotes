package com.jenzz.peoplenotes.feature.notes.ui

import androidx.annotation.StringRes
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.SortBy
import com.jenzz.peoplenotes.common.ui.SortByState
import com.jenzz.peoplenotes.common.ui.TextResource

enum class NotesSortBy(@StringRes label: Int) {

    MostRecentFirst(R.string.most_recent_first),
    OldestFirst(R.string.oldest_first);

    val label: TextResource = TextResource.fromId(label)

    companion object {

        val DEFAULT = MostRecentFirst
    }
}

fun NotesSortBy.Companion.toSortByState(): SortByState =
    SortByState(
        items = NotesSortBy.values()
            .map { sortBy ->
                SortBy(sortBy.label, sortBy == NotesSortBy.MostRecentFirst)
            }
    )

fun SortBy.toNotesSortBy(): NotesSortBy =
    NotesSortBy.values()
        .single { sortBy ->
            sortBy.label == label
        }
