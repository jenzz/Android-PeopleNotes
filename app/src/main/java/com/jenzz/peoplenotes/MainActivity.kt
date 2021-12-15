package com.jenzz.peoplenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.feature.add_person.AddPersonFeature
import com.jenzz.peoplenotes.feature.home.HomeFeature
import com.jenzz.peoplenotes.feature.settings.SettingsFeature
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PeopleNotesTheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen() {
    val navController = rememberNavController()
    val features = listOf(
        HomeFeature,
        AddPersonFeature,
        SettingsFeature,
    )

    NavHost(
        navController = navController,
        startDestination = features.first().route,
    ) {
        features.forEach { feature ->
            composable(route = feature.route) {
                feature.Content(navController)
            }
        }
    }
}
