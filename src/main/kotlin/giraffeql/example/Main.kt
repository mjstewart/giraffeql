package giraffeql.example

import giraffeql.annotation.GQLType
import giraffeql.configuration.SchemaConfiguration
import giraffeql.resolver.*
import graphql.schema.idl.SchemaPrinter
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.scanners.TypeElementsScanner

fun main() {
    val config = object : SchemaConfiguration {
        override fun packagesToScan(): List<String> = listOf("giraffeql.example")
    }

    // TODO: iterate over many packages, not just first.
    val r = Reflections(config.packagesToScan().first(), TypeElementsScanner(), SubTypesScanner(), TypeAnnotationsScanner())

    val kClasses = r.getTypesAnnotatedWith(GQLType::class.java).map { it.kotlin }



//    val resolver = GQLTypeResolver(types = ok.asSequence())

    val resolver = ComposedTypeResolver()
    val env = TypeResolverEnvironment(resolver, config)

//    ok.forEach {
//         when (val result = resolver.resolve(it, env)) {
//             is GQLResolvedType.Output -> println(SchemaPrinter().print(result.type))
//             is GQLResolvedType.Input -> println(SchemaPrinter().print(result.type))
//             is GQLResolvedType.None -> println("Not resolved")
//         }
//    }

}

enum class Day {
    YOU
}
interface Me {
    fun x(): String
}

data class Hello(val x: String)
class Hello2(val x: String)

