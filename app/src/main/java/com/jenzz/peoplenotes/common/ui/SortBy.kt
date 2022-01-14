package com.jenzz.peoplenotes.common.ui

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SortBy(
    val label: TextResource,
    val isSelected: Boolean,
) : Parcelable

@Parcelize
data class SortByUiState(
    val items: List<SortBy>,
) : Parcelable {

    @IgnoredOnParcel
    val selected: SortBy = items.single { item -> item.isSelected }
}
