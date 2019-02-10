package giraffeql.resolver

import giraffeql.configuration.SchemaConfiguration

data class TypeResolverEnvironment(
        val resolver: TypeResolver,
        val config: SchemaConfiguration,
        val cache: KClassCache = KClassCache()
)