package com.jenzz.peoplenotes.common.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class SuffixVisualTransformation(
    private val text: String,
    private val suffix: String,
) : () -> VisualTransformation {

    override fun invoke(): VisualTransformation =
        if (text.isEmpty())
            VisualTransformation.None
        else
            VisualTransformation { text ->
                TransformedText(
                    text = AnnotatedString(text.text + suffix),
                    offsetMapping = OffsetMapping.Identity,
                )
            }
}