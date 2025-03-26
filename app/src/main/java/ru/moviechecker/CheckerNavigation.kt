package ru.moviechecker

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class CheckerNavigationActions(navController: NavHostController) {
    val navigateToMovies: () -> Unit = {
        navController.navigate(route = MoviesRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}