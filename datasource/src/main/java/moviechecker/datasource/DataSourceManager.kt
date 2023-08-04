package moviechecker.datasource

import moviechecker.core.di.datasource.DataSource
import moviechecker.lostfilm.LostfilmDataSource

class DataSourceManager {
    val dataSources: MutableList<DataSource> = mutableListOf()

    init {
        //TODO: how to add sources dynamically? When instances may be created? In dagger/hilt module?
        dataSources.add(LostfilmDataSource())
    }

}