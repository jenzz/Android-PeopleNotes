package com.jenzz.peoplenotes.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showLongToast(@StringRes text: Int) {
    Toast.makeText(this, getString(text), Toast.LENGTH_LONG).show()
}
