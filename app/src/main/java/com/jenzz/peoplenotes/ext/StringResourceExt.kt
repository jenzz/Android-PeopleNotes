package com.jenzz.peoplenotes.ext

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
@ReadOnlyComposable
fun stringResourceWithStyledPlaceholders(
    @StringRes id: Int,
    spanStyle: (Int) -> SpanStyle,
    vararg formatArgs: Any,
): AnnotatedString {
    val text = stringResource(id, *formatArgs)
    var startIndex = 0
    val spanStyles = formatArgs.mapIndexed { i, formatArg ->
        val arg = formatArg.toString()
        val start = text.indexOf(arg, startIndex)
        val end = start + arg.length
        startIndex = end
        AnnotatedString.Range(
            item = spanStyle(i),
            start = start,
            end = end,
        )
    }
    return AnnotatedString(text = text, spanStyles = spanStyles)
}

@Composable
@ReadOnlyComposable
fun stringResourceWithBoldPlaceholders(
    @StringRes id: Int,
    vararg formatArgs: Any,
): AnnotatedString =
    stringResourceWithStyledPlaceholders(
        id = id,
        spanStyle = { SpanStyle(fontWeight = FontWeight.Bold) },
        formatArgs = formatArgs,
    )
