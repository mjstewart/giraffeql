package giraffeql.configuration

interface SchemaConfiguration {
    fun packagesToScan(): List<String> = emptyList()
}