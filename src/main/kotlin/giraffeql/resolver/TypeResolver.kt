package giraffeql.resolver

import giraffeql.extensions.toKClass
import giraffeql.extensions.wrapNonNull
import graphql.schema.GraphQLType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

interface TypeResolver {
    fun resolve(parent: KClass<*>, property: KProperty1<*, *>, env: TypeResolverEnvironment): GraphQLType?
            = resolve(property.returnType, env)

    fun resolve(type: KType, env: TypeResolverEnvironment): GraphQLType? =
            resolve(type.toKClass(), env)?.wrapNonNull(type)

    fun resolve(kClass: KClass<*>, env: TypeResolverEnvironment): GraphQLType? = null
}




