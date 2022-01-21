package com.jenzz.peoplenotes.ext

import android.content.Context
import android.widget.Toast

fun Context.showShortToast(text: CharSequence) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
