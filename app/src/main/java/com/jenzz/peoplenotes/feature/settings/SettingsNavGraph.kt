package com.jenzz.peoplenotes.feature.settings

import com.jenzz.peoplenotes.Content
import com.jenzz.peoplenotes.NavGraph
import com.jenzz.peoplenotes.NavGraphRoute
import com.jenzz.peoplenotes.feature.settings.ui.SettingsScreen

object SettingsNavGraph : NavGraph {

    override val route: NavGraphRoute = NavGraphRoute("settings")

    override val rootScreen: Content = { _, _ ->
        SettingsScreen()
    }
}