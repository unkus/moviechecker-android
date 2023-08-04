package moviechecker.core.di.database

import moviechecker.core.di.datasource.DataRecord

interface CheckerDatabase {
    suspend fun populateDatabase(records: Collection<DataRecord>)
}