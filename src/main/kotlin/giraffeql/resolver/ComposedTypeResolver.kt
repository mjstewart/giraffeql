package giraffeql.resolver

import graphql.schema.GraphQLType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

class ComposedTypeResolver(val resolvers: List<TypeResolver> = emptyList()) : TypeResolver {

    companion object {
        fun defaultResolver(): ComposedTypeResolver = ComposedTypeResolver(listOf(
                ScalarTypeResolver(),
                ObjectTypeResolver(),
                InputObjectTypeResolver(),
                IDTypeResolver(),
                EnumTypeResolver(),
                ListLikeTypeResolver(),
                OptionalTypeResolver()
        ))
    }

    private fun reduce(f: (TypeResolver) -> GraphQLType?): GraphQLType? =
            resolvers.fold(null) { _, resolver ->
                when (val result = f(resolver)) {
                    null -> null
                    else -> return result
                }
            }

    override fun resolve(kClass: KClass<*>, env: TypeResolverEnvironment): GraphQLType? =
            reduce { it.resolve(kClass, env) }

    override fun resolve(type: KType, env: TypeResolverEnvironment): GraphQLType? =
            reduce { it.resolve(type, env) }

    override fun resolve(parent: KClass<*>, property: KProperty1<*, *>, env: TypeResolverEnvironment): GraphQLType? =
            reduce { it.resolve(parent, property, env) }
}