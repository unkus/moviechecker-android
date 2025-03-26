package ru.moviechecker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.moviechecker.database.AppContainer
import ru.moviechecker.ui.movie.MovieCardsViewModel
import ru.moviechecker.ui.movie.MovieDetailsRoute
import ru.moviechecker.ui.movie.MovieDetailsViewModel
import ru.moviechecker.ui.movie.MoviesRoute

@Serializable
object MoviesRoute

@Serializable
data class MovieDetailsRoute(val id: Int)

@Composable
fun CheckerNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = MoviesRoute,
        modifier = modifier
    ) {
        composable<MoviesRoute> {
            val moviesViewModel: MovieCardsViewModel = viewModel(
                factory = MovieCardsViewModel.provideFactory(
                    moviesRepository = appContainer.moviesRepository,
                    episodesRepository = appContainer.episodesRepository
                )
            )
            MoviesRoute(
                viewModel = moviesViewModel,
                openDrawer = openDrawer,
                navigateToMovieDetails = { movieId ->
                    navController.navigate(
                        route = MovieDetailsRoute(id = movieId)
                    )
                }
            )
        }
        composable<MovieDetailsRoute> { navBackStack ->
            val movie = navBackStack.toRoute<MovieDetailsRoute>()
            navBackStack.savedStateHandle["id"] = movie.id
            val movieDetailsViewModel: MovieDetailsViewModel = viewModel(
                factory = MovieDetailsViewModel.provideFactory(
                    savedStateHandle = navBackStack.savedStateHandle,
                    moviesRepository = appContainer.moviesRepository,
                    seasonsRepository = appContainer.seasonsRepository,
                    episodesRepository = appContainer.episodesRepository
                )
            )
            MovieDetailsRoute(
                viewModel = movieDetailsViewModel,
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}