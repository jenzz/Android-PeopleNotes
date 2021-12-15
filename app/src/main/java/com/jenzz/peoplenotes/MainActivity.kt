package com.jenzz.peoplenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jenzz.peoplenotes.feature.home.HomeFeature
import com.jenzz.peoplenotes.feature.settings.SettingsFeature
import com.jenzz.peoplenotes.ui.theme.PeopleNotesTheme
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
fun MainScreen() {
    val navController = rememberNavController()
    val features = listOf(
        HomeFeature,
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
