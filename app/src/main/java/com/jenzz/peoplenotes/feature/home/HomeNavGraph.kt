package com.jenzz.peoplenotes.feature.home

import com.jenzz.peoplenotes.*
import com.jenzz.peoplenotes.feature.add_person.ui.AddPersonScreen
import com.jenzz.peoplenotes.feature.home.ui.HomeScreen
import com.jenzz.peoplenotes.feature.settings.SettingsNavGraph

object HomeNavGraph : NavGraph {

    override val route: NavGraphRoute = NavGraphRoute("home")

    override val rootScreen: Content = { navController, _ ->
        HomeScreen(
            onAddPersonManuallyClick = {
                navController.navigate("add_person")
            },
            onSettingsClick = {
                navController.navigate(SettingsNavGraph.route.value)
            }
        )
    }

    override val additionalScreens: Screens =
        Screens(
            Screen(
                route = ScreenRoute("add_person"),
            ) { navController, _ ->
                AddPersonScreen(
                    onPersonAdded = {
                        navController.navigate(route.value) {
                            popUpTo(route.value) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        )
}