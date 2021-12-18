package com.jenzz.peoplenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.feature.add_person.AddPersonFeature
import com.jenzz.peoplenotes.feature.home.HomeFeature
import com.jenzz.peoplenotes.feature.settings.SettingsFeature
import com.jenzz.peoplenotes.feature.settings.data.SettingsRepository
import com.jenzz.peoplenotes.feature.settings.data.ThemePreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }

    @Composable
    private fun App() {
        val coroutineScope = rememberCoroutineScope()
        var theme by remember { mutableStateOf(ThemePreference.DEFAULT) }
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                settingsRepository.settings.collect { settings ->
                    theme = settings.theme
                }
            }
        }
        PeopleNotesTheme(
            theme = theme
        ) {
            MainScreen()
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
