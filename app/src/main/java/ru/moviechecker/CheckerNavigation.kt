package ru.moviechecker

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class CheckerNavigationActions(navController: NavHostController) {
    val navigateToSites: () -> Unit = {
        navController.navigate(route = SitesRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToMovies: () -> Unit = {
        navController.navigate(route = MoviesRoute(siteId = null)) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}