package com.jenzz.peoplenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.feature.NavGraphs
import com.jenzz.peoplenotes.feature.settings.data.Settings
import com.jenzz.peoplenotes.feature.settings.data.SettingsRepository
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Workaround for Robolectric & Hilt in JVM tests: https://github.com/google/dagger/issues/1956
@AndroidEntryPoint(ComponentActivity::class)
class MainActivity : Hilt_MainActivity() {

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
        val settingsState = settingsRepository
            .observeSettings()
            .collectAsState(initial = Settings())
        PeopleNotesTheme(
            theme = settingsState.value.theme
        ) {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                engine = rememberAnimatedNavHostEngine(
                    rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
                ),
            )
        }
    }
}
