package com.jenzz.peoplenotes.feature.home

import com.jenzz.peoplenotes.*
import com.jenzz.peoplenotes.feature.add_person.ui.AddPersonScreen
import com.jenzz.peoplenotes.feature.home.ui.HomeScreen
import com.jenzz.peoplenotes.feature.person_details.ui.PersonDetailsScreen
import com.jenzz.peoplenotes.feature.settings.SettingsNavGraph

object HomeNavGraph : NavGraph {

    private val addPersonScreen = Screen(
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

    private val personDetailsScreen = Screen(
        route = ScreenRoute("person_details"),
    ) { navController, _ ->
        PersonDetailsScreen()
    }

    override val route: NavGraphRoute = NavGraphRoute("home")

    override val rootScreen: Content = { navController, _ ->
        HomeScreen(
            onClick = { person ->
                navController.navigate(personDetailsScreen.route.value)
            },
            onAddPersonManuallyClick = {
                navController.navigate(addPersonScreen.route.value)
            },
            onSettingsClick = {
                navController.navigate(SettingsNavGraph.route.value)
            },
        )
    }

    override val additionalScreens: Screens =
        Screens(
            addPersonScreen,
            personDetailsScreen,
        )
}