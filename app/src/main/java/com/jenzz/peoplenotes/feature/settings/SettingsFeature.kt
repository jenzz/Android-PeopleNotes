package com.jenzz.peoplenotes.feature.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.jenzz.peoplenotes.feature.Feature
import com.jenzz.peoplenotes.feature.home.HomeFeature

object SettingsFeature : Feature {

    override val route: String = "settings"

    @Composable
    override fun Content(navController: NavHostController) {
        SettingsScreen(
            onHomeClicked = {
                navController.navigate(HomeFeature.route)
            }
        )
    }
}
