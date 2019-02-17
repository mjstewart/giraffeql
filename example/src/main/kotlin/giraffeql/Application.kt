package giraffeql

import giraffeql.configuration.SchemaConfiguration
import graphql.schema.GraphQLScalarType

class Application

fun main() {
    val config = object : SchemaConfiguration {
        override fun packagesToScan(): List<String> = listOf("giraffeql")

        override fun scalars(): List<GraphQLScalarType> = listOf()

        // TODO: add configuration id
    }

    generateSchema(config)
}


