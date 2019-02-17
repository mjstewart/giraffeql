package demo

import giraffeql.configuration.SchemaConfiguration
import giraffeql.generateSchema
import graphql.schema.GraphQLScalarType

class Application

fun main() {
    val config = object : SchemaConfiguration {
        override fun packagesToScan(): List<String> = listOf("example.demo.domain")

        override fun scalars(): List<GraphQLScalarType> = listOf()

        // TODO: add configuration id
    }

    generateSchema(config)
}


