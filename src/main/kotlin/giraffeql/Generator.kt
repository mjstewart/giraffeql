package giraffeql

import giraffeql.annotation.GQLType
import giraffeql.configuration.SchemaConfiguration
import giraffeql.resolver.ComposedTypeResolver
import giraffeql.resolver.TypeResolverEnvironment
import graphql.schema.idl.SchemaPrinter
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper

fun generateSchema(config: SchemaConfiguration) {
    // TODO: iterate over many packages, not just first.

    val classes = config.packagesToScan().flatMap { path ->
        println("path=$path")
        println(ClasspathHelper.forPackage("giraffeql.example.demo.domain").size)
        Reflections("demo.domain").getTypesAnnotatedWith(GQLType::class.java).map { it.kotlin }
    }

    val resolver = ComposedTypeResolver.defaultResolver()
    val env = TypeResolverEnvironment(resolver, config)

    val results = classes.mapNotNull { resolver.resolve(it, env) }

//    GraphQLCodeRegistry.newCodeRegistry()
//            .dataFetchers()
//            .build()
//
//    RuntimeWiring.newRuntimeWiring()
//            .sc

    results.forEach {
        println("-----")
        println(SchemaPrinter().print(it))
    }
}