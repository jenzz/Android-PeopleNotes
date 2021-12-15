package com.jenzz.peoplenotes.feature.home.ui

import androidx.annotation.StringRes
import com.jenzz.peoplenotes.R

enum class SortBy(@StringRes val label: Int) {

    LastModified(R.string.last_modified),
    FirstName(R.string.first_name),
    LastName(R.string.last_name),
}
