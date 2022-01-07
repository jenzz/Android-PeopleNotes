package com.jenzz.peoplenotes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.jenzz.peoplenotes.NavGraph
import com.jenzz.peoplenotes.ScreenRoute
import com.jenzz.peoplenotes.ext.checkNoDuplicates
import com.jenzz.peoplenotes.feature.home.HomeNavGraph
import com.jenzz.peoplenotes.feature.settings.SettingsNavGraph

object Navigation {

    private val ROOT = ScreenRoute("root")

    private val navGraphs = listOf(
        HomeNavGraph,
        SettingsNavGraph,
    ).checkNoDuplicates(NavGraph::route) { duplicates ->
        "Navigation graph routes must be unique, but found duplicate entries: ${duplicates}."
    }

    val root = HomeNavGraph.route

    fun install(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphs.forEach { navGraph ->
            navGraphBuilder.navigation(
                route = navGraph.route.value,
                startDestination = ROOT.value,
            ) {
                composable(
                    route = ROOT.value
                ) { navBackStack ->
                    navGraph.rootScreen(navController, navBackStack)
                }
                navGraph.additionalScreens.value.forEach { screen ->
                    check(screen.route != ROOT) { "Route called 'root' is reserved for NavGraph.rootScreen." }
                    composable(
                        route = screen.route.value,
                    ) { navBackStack ->
                        screen.content(navController, navBackStack)
                    }
                }
            }
        }
    }
}