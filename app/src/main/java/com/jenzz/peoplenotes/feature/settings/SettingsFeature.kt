package com.jenzz.peoplenotes.feature.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jenzz.peoplenotes.feature.Feature
import com.jenzz.peoplenotes.feature.settings.ui.SettingsScreen

object SettingsFeature : Feature {

    override val route: String = "settings"

    @Composable
    override fun Content(
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry,
    ) {
        SettingsScreen()
    }
}
