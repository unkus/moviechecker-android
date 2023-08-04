package moviechecker.core.di.datasource

interface DataSource {
    fun retrieveData(): Collection<DataRecord>
}