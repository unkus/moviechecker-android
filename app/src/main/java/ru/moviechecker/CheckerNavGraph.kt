package ru.moviechecker

import android.util.Log
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
import ru.moviechecker.ui.site.SitesRoute
import ru.moviechecker.ui.site.SitesViewModel

@Serializable
object SitesRoute

@Serializable
data class MoviesRoute(val siteId: Int?)

@Serializable
data class MovieDetailsRoute(val movieId: Int)

@Composable
fun CheckerNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = MoviesRoute(siteId = null),
        modifier = modifier
    ) {
        composable<SitesRoute> {
            val sitesViewModel: SitesViewModel = viewModel(
                factory = SitesViewModel.provideFactory(
                    sitesRepository = appContainer.sitesRepository
                )
            )
            SitesRoute(
                viewModel = sitesViewModel,
                openDrawer = openDrawer,
                navigateToMovies = { siteId ->
                    navController.navigate(
                        route = MoviesRoute(siteId = siteId)
                    )
                }
            )
        }
        composable<MoviesRoute> { navBackStack ->
            val route = navBackStack.toRoute<MoviesRoute>()
            navBackStack.savedStateHandle["siteId"] = route.siteId
            val moviesViewModel: MovieCardsViewModel = viewModel(
                factory = MovieCardsViewModel.provideFactory(
                    savedStateHandle = navBackStack.savedStateHandle,
                    sitesRepository = appContainer.sitesRepository,
                    moviesRepository = appContainer.moviesRepository,
                    episodesRepository = appContainer.episodesRepository
                )
            )
            MoviesRoute(
                viewModel = moviesViewModel,
                openDrawer = openDrawer,
                navigateToMovieDetails = { movieId ->
                    navController.navigate(
                        route = MovieDetailsRoute(movieId = movieId)
                    )
                },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable<MovieDetailsRoute> { navBackStack ->
            val movie = navBackStack.toRoute<MovieDetailsRoute>()
            navBackStack.savedStateHandle["id"] = movie.movieId
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