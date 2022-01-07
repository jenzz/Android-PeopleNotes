package com.jenzz.peoplenotes.ext

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.thenIf(
    condition: Boolean,
    then: Modifier.() -> Modifier,
): Modifier =
    if (condition) {
        then()
    } else {
        this
    }

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.noRippleClickable(
    onClick: () -> Unit
): Modifier =
    composed {
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick,
        )
    }