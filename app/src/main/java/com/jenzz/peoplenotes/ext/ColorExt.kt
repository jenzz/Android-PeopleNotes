package com.jenzz.peoplenotes.ext

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun Color.Companion.random(): Color =
    Color(
        red = Random.nextInt(256),
        green = Random.nextInt(256),
        blue = Random.nextInt(256),
    )
