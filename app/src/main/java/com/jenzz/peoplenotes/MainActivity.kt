package com.jenzz.peoplenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.feature.settings.data.Settings
import com.jenzz.peoplenotes.feature.settings.data.SettingsRepository
import com.jenzz.peoplenotes.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
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
        val settingsState = settingsRepository.settings.collectAsState(initial = Settings.DEFAULT)
        PeopleNotesTheme(
            theme = settingsState.value.theme
        ) {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Navigation.root.value,
    ) {
        Navigation.install(this, navController)
    }
}
