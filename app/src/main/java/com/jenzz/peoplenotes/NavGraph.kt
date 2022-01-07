package com.jenzz.peoplenotes

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jenzz.peoplenotes.ext.checkNoDuplicates

typealias Content = @Composable (NavHostController, NavBackStackEntry) -> Unit

interface NavGraph {

    val route: NavGraphRoute

    val rootScreen: Content

    val additionalScreens: Screens
        get() = Screens()
}

@JvmInline
value class NavGraphRoute(val value: String)

@JvmInline
value class ScreenRoute(val value: String)

data class Screen(
    val route: ScreenRoute,
    val content: Content,
)

@JvmInline
value class Screens(val value: List<Screen>) {

    constructor(vararg values: Screen) : this(values.toList())

    init {
        value.checkNoDuplicates(Screen::route) { duplicates ->
            "Screen routes must be unique, but found duplicate entries: ${duplicates}."
        }
    }
}