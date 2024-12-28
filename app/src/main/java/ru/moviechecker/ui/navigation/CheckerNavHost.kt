package ru.moviechecker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.moviechecker.ui.episode.EpisodeDetailsDestination
import ru.moviechecker.ui.episode.EpisodeDetailsScreen
import ru.moviechecker.ui.episode.EpisodeEditDestination
import ru.moviechecker.ui.episode.EpisodeEditScreen
import ru.moviechecker.ui.episode.EpisodeEntryDestination
import ru.moviechecker.ui.episode.EpisodeEntryScreen
import ru.moviechecker.ui.home.HomeDestination
import ru.moviechecker.ui.home.HomeScreen
import ru.moviechecker.ui.movie.MovieDetailsDestination
import ru.moviechecker.ui.movie.MovieDetailsScreen
import ru.moviechecker.ui.movie.MoviesDestination
import ru.moviechecker.ui.movie.MoviesScreen

@Composable
fun CheckerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MoviesDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToEpisodeUpdate = {
                    navController.navigate("${EpisodeDetailsDestination.route}/${it}")
                }
            )
        }
        composable(route = MoviesDestination.route) {
            MoviesScreen(
                navigateToMovieDetails = {
                    navController.navigate("${MovieDetailsDestination.route}/${it}")
                }
            )
        }
        composable(
            route = MovieDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(MovieDetailsDestination.movieIdArg) {
                type = NavType.IntType
            })
        ) {
            MovieDetailsScreen(
                navigateToMovieEdit = { },
                navigateBack = { navController.navigateUp() }
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