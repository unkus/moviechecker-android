package ru.moviechecker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import ru.moviechecker.database.AppContainer
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.MovieCard
import ru.moviechecker.database.movies.MovieDetails
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.seasons.SeasonWithEpisodes
import ru.moviechecker.database.seasons.SeasonsRepository
import ru.moviechecker.database.sites.SiteEntity
import ru.moviechecker.database.sites.SitesRepository
import ru.moviechecker.ui.SearchAction
import ru.moviechecker.ui.ShowNonFavoritesAction
import ru.moviechecker.ui.ShowViewedAction
import ru.moviechecker.ui.catalog.CatalogDestination
import ru.moviechecker.ui.main.MainScreen
import ru.moviechecker.ui.movie.MoviesViewModel
import ru.moviechecker.ui.new_releases.NewReleasesDestination
import ru.moviechecker.ui.new_releases.NewReleasesViewModel
import ru.moviechecker.ui.site.SitesDestination
import ru.moviechecker.ui.site.SitesViewModel
import ru.moviechecker.ui.theme.MoviecheckerTheme

enum class AppDestinations(
    val label: Int,
    val icon: Int
) {
    NEW_RELEASES(
        R.string.new_releases,
        R.drawable.menu_24px
    ),
    CATALOG(
        R.string.catalog,
        R.drawable.menu_24px
    ),
    SITES(R.string.sites, R.drawable.menu_24px),
}

/**
 * Top level composable that represents screens for the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRoot(
    appContainer: AppContainer
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.NEW_RELEASES) }
    val scope = rememberCoroutineScope()

    val navigationSuiteScaffoldState =
        rememberNavigationSuiteScaffoldState()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    selected = currentDestination == destination,
                    onClick = {
                        currentDestination = destination
                        scope.launch {
                            navigationSuiteScaffoldState.hide()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(destination.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(id = destination.label)) }
                )
            }
        }
    ) {
        MainScreen(
            label = currentDestination.label,
            actions = {
                when (currentDestination) {

                    AppDestinations.NEW_RELEASES -> {
                        ShowNonFavoritesAction()
                    }

                    AppDestinations.CATALOG -> {
                        ShowNonFavoritesAction()
                        ShowViewedAction()
                        SearchAction()
                    }

                    else -> {}
                }
            }
        ) { innerPadding ->
            when (currentDestination) {
                AppDestinations.NEW_RELEASES -> NewReleasesDestination(
                    innerPadding = innerPadding,
                    viewModel = viewModel(
                        factory = NewReleasesViewModel.provideFactory(
                            moviesRepository = appContainer.moviesRepository,
                            episodesRepository = appContainer.episodesRepository
                        )
                    )
                )

                AppDestinations.CATALOG -> CatalogDestination(
                    innerPadding = innerPadding,
                    viewModel = viewModel(
                        factory = MoviesViewModel.provideFactory(
                            moviesRepository = appContainer.moviesRepository,
                            episodesRepository = appContainer.episodesRepository
                        )
                    )
                )

                AppDestinations.SITES -> SitesDestination(
                    innerPadding = innerPadding,
                    viewModel = viewModel(factory = SitesViewModel.provideFactory(appContainer.sitesRepository))
                )
            }
        }
    }
}

@Preview
@Composable
fun NavigationRootPreview(
    @PreviewParameter(NavigationRootPreviewParameterProvider::class) destination: Int
) {
    MoviecheckerTheme {
        NavigationRoot(
            appContainer = object : AppContainer {
                override val episodesRepository: EpisodesRepository
                    get() = object : EpisodesRepository {
                        override fun getAllStream(): Flow<List<EpisodeEntity>> {
                            return emptyFlow()
                        }

                        override fun getByIdStream(id: Int): Flow<EpisodeEntity> {
                            return emptyFlow()
                        }

                        override fun getEpisodesBySeasonIdStream(seasonId: Int): Flow<List<EpisodeEntity>> {
                            return emptyFlow()
                        }

                        override fun getEpisodesBySeasonIdStream(seasonIds: List<Int>): Flow<List<EpisodeEntity>> {
                            return emptyFlow()
                        }

                        override fun getById(id: Int): EpisodeEntity {
                            TODO("Not yet implemented")
                        }

                        override fun insertEpisode(episode: EpisodeEntity) {
                            TODO("Not yet implemented")
                        }

                        override fun updateEpisode(episode: EpisodeEntity) {
                            TODO("Not yet implemented")
                        }

                        override fun deleteEpisode(episode: EpisodeEntity) {
                            TODO("Not yet implemented")
                        }

                        override fun updateEpisodeState(
                            episodeId: Int,
                            newState: EpisodeState
                        ) {
                            TODO("Not yet implemented")
                        }
                    }
                override val seasonsRepository: SeasonsRepository
                    get() = object : SeasonsRepository {
                        override fun updateSeason(season: SeasonEntity) {
                            TODO("Not yet implemented")
                        }

                        override fun getSeasonsByMovieId(movieId: Int): List<SeasonEntity> {
                            TODO("Not yet implemented")
                        }

                        override fun getSeasonsWithEpisodesByMovieId(movieId: Int): List<SeasonWithEpisodes> {
                            TODO("Not yet implemented")
                        }

                        override fun getNumberOfSeasonsByMovieIdStream(movieId: Int): Flow<Int> {
                            return emptyFlow()
                        }
                    }
                override val moviesRepository: MoviesRepository
                    get() = object : MoviesRepository {
                        override fun getById(id: Int): MovieEntity {
                            TODO("Not yet implemented")
                        }

                        override fun getAll(): List<MovieEntity> {
                            TODO("Not yet implemented")
                        }

                        override fun updateMovie(movie: MovieEntity) {
                            TODO("Not yet implemented")
                        }

                        override fun toggleFavoritesMark(movieId: Int) {
                            TODO("Not yet implemented")
                        }

                        override fun getMovieDetails(id: Int): MovieDetails {
                            TODO("Not yet implemented")
                        }

                        override fun getMovieCardStream(): Flow<List<MovieCard>> {
                            return emptyFlow()
                        }

                        override fun getNewReleasesStream(): Flow<List<MovieCard>> {
                            return emptyFlow()
                        }
                    }
                override val sitesRepository: SitesRepository
                    get() = object : SitesRepository {
                        override fun findById(id: Int): SiteEntity? {
                            TODO("Not yet implemented")
                        }

                        override fun getByIdStream(id: Int): Flow<SiteEntity> {
                            return emptyFlow()
                        }

                        override fun getAllStream(): Flow<List<SiteEntity>> {
                            return emptyFlow()
                        }

                        override fun updateSite(site: SiteEntity) {
                            TODO("Not yet implemented")
                        }
                    }
            })
    }
}

class NavigationRootPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values = AppDestinations.entries
        .map(AppDestinations::ordinal)
        .asSequence()
}
