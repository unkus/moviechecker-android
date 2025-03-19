package ru.moviechecker

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object CheckerDestinations {
    const val MOVIES_ROUTE = "movies"
}

class CheckerNavigationActions(navController: NavHostController) {
    val navigateToMovies: () -> Unit = {
        navController.navigate(CheckerDestinations.MOVIES_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}