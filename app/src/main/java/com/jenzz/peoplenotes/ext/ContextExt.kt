package com.jenzz.peoplenotes.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showLongToast(@StringRes text: Int) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.showLongToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
