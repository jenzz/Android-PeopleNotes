package com.jenzz.peoplenotes.feature

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface Feature {

    val route: String

    @Composable
    fun Content(navController: NavHostController)
}
