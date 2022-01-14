package com.jenzz.peoplenotes.feature.people.ui

import androidx.annotation.StringRes
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.SortBy
import com.jenzz.peoplenotes.common.ui.TextResource

enum class PeopleSortBy(@StringRes label: Int) {

    LastModified(R.string.last_modified),
    FirstName(R.string.first_name),
    LastName(R.string.last_name);

    val label: TextResource = TextResource.fromId(label)

    companion object {

        val DEFAULT = LastModified
    }
}

fun PeopleSortBy.Companion.toSortBy(): List<SortBy> =
    PeopleSortBy.values()
        .map { sortBy ->
            SortBy(sortBy.label, sortBy == PeopleSortBy.LastModified)
        }

fun SortBy.toPeopleSortBy(): PeopleSortBy =
    PeopleSortBy.values().single { sortBy -> sortBy.label == label }
