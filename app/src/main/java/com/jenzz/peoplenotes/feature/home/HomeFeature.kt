package com.jenzz.peoplenotes.feature.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.jenzz.peoplenotes.feature.Feature
import com.jenzz.peoplenotes.feature.home.ui.HomeScreen
import com.jenzz.peoplenotes.feature.settings.SettingsFeature

object HomeFeature : Feature {

    override val route: String = "home"

    @Composable
    override fun Content(navController: NavHostController) {
        HomeScreen(
            onSettingsClick = {
                navController.navigate(SettingsFeature.route)
            }
        )
    }
}
