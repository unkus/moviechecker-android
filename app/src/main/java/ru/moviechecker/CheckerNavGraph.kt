package ru.moviechecker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.moviechecker.CheckerDestinations.MOVIES_ROUTE
import ru.moviechecker.database.AppContainer
import ru.moviechecker.ui.movie.MovieCardsViewModel
import ru.moviechecker.ui.movie.MoviesRoute

@Composable
fun CheckerNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = MOVIES_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = MOVIES_ROUTE,
        ) {
            val moviesViewModel: MovieCardsViewModel = viewModel(
                factory = MovieCardsViewModel.provideFactory(
                    moviesRepository = appContainer.moviesRepository,
                    episodesRepository = appContainer.episodesRepository
                )
            )
            MoviesRoute(
                viewModel = moviesViewModel,
                openDrawer = openDrawer
            )
        }
    }
}