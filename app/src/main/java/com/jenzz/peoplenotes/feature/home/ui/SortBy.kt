package com.jenzz.peoplenotes.feature.home.ui

import androidx.annotation.StringRes
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.TextResource

enum class SortBy(@StringRes label: Int) {

    LastModified(R.string.last_modified),
    FirstName(R.string.first_name),
    LastName(R.string.last_name);

    val label: TextResource = TextResource.fromId(label)

    companion object {

        val DEFAULT = LastModified
    }
}
