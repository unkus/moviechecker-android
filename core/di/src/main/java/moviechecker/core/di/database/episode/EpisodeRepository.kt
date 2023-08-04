package moviechecker.core.di.database.episode

import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    val released: Flow<List<Episode>>
    val expected: Flow<List<Episode>>
}