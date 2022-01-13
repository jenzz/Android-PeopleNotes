package com.jenzz.peoplenotes.ext

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

@Composable
@ReadOnlyComposable
fun stringResourceWithStyledPlaceholders(
    @StringRes id: Int,
    spanStyle: (Int) -> SpanStyle,
    vararg formatArgs: Any,
): AnnotatedString {
    val text = stringResource(id, *formatArgs)
    val spanStyles = formatArgs.mapIndexed { i, formatArg ->
        val placeholder = formatArg.toString()
        val start = text.indexOf(placeholder)
        AnnotatedString.Range(
            item = spanStyle(i),
            start = start,
            end = start + placeholder.length,
        )
    }
    return AnnotatedString(text = text, spanStyles = spanStyles)
}