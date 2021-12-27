package com.jenzz.peoplenotes.feature

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import com.jenzz.peoplenotes.ext.checkUnique
import com.jenzz.peoplenotes.feature.add_person.AddPersonFeature
import com.jenzz.peoplenotes.feature.home.HomeFeature
import com.jenzz.peoplenotes.feature.settings.SettingsFeature

interface Feature {

    companion object {

        val allFeatures = listOf(
            HomeFeature,
            AddPersonFeature,
            SettingsFeature,
        ).checkUnique(Feature::route) { "Features must not have duplicate routes." }
    }

    val route: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val deepLinks: List<NavDeepLink>
        get() = emptyList()

    @Composable
    fun Content(
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry,
    )
}
