package giraffeql.resolver

import giraffeql.extensions.toKClass
import giraffeql.extensions.wrapNonNull
import graphql.Scalars
import graphql.schema.GraphQLType
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType

class IDTypeResolver(
        private val supportedIDs: Set<KClass<*>> = setOf(Int::class, Long::class, String::class, UUID::class)
) : TypeResolver {

    private fun isSupportedID(kClass: KClass<*>): Boolean = kClass in supportedIDs

    override fun resolve(parent: KClass<*>, property: KProperty1<*, *>, env: TypeResolverEnvironment): GraphQLType? =
            if (property.name.toLowerCase().endsWith("id") && isSupportedID(property.returnType.toKClass())) {
                println("is supported ${parent.simpleName}, ${property.name}, ${property.returnType}")
                Scalars.GraphQLID.wrapNonNull(property.returnType)
            } else {
                null
            }

    override fun resolve(type: KType, env: TypeResolverEnvironment): GraphQLType? = null

    override fun resolve(kClass: KClass<*>, env: TypeResolverEnvironment): GraphQLType? = null
}
