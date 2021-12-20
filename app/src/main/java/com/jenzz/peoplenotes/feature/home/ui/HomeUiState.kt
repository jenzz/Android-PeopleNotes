package com.jenzz.peoplenotes.feature.home.ui

import android.os.Parcelable
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.ext.PartialSavedState
import kotlinx.parcelize.Parcelize

data class HomeUiState(
    val isLoading: Boolean,
    val filter: String,
    val listStyle: ListStyle,
    val sortBy: SortBy,
    val people: List<Person>,
) : PartialSavedState<HomeUiState, HomeSavedState> {

    val isEmpty: Boolean = people.isEmpty()

    val showActions: Boolean = !isLoading

    override val savedState: HomeSavedState =
        HomeSavedState(
            filter = filter,
            listStyle = listStyle,
            sortBy = sortBy
        )

    override fun mergeWithSavedState(savedState: HomeSavedState): HomeUiState =
        copy(
            filter = savedState.filter,
            listStyle = savedState.listStyle,
            sortBy = savedState.sortBy,
        )
}

@Parcelize
data class HomeSavedState(
    val filter: String,
    val listStyle: ListStyle,
    val sortBy: SortBy,
) : Parcelable
