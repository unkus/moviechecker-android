package com.moviechecker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.moviechecker.ui.episode.EpisodeDetailsDestination
import com.moviechecker.ui.episode.EpisodeDetailsScreen
import com.moviechecker.ui.episode.EpisodeEditDestination
import com.moviechecker.ui.episode.EpisodeEditScreen
import com.moviechecker.ui.episode.EpisodeEntryDestination
import com.moviechecker.ui.episode.EpisodeEntryScreen
import com.moviechecker.ui.home.HomeDestination
import com.moviechecker.ui.home.HomeScreen

@Composable
fun CheckerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToEpisodeUpdate = {
                    navController.navigate("${EpisodeDetailsDestination.route}/${it}")
                }
            )
        }
        composable(route = EpisodeEntryDestination.route) {
            EpisodeEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = EpisodeDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(EpisodeDetailsDestination.episodeIdArg) {
                type = NavType.IntType
            })
        ) {
            EpisodeDetailsScreen(
                navigateToEditEpisode = { navController.navigate("${EpisodeEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = EpisodeEditDestination.routeWithArgs,
            arguments = listOf(navArgument(EpisodeEditDestination.episodeIdArg) {
                type = NavType.IntType
            })
        ) {
            EpisodeEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}