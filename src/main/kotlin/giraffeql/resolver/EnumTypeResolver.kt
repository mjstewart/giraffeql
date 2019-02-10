package giraffeql.resolver

import giraffeql.extensions.getNameOrThrow
import giraffeql.extensions.isEnum
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLType
import kotlin.reflect.KClass

class EnumTypeResolver : TypeResolver {

    override fun resolve(kClass: KClass<*>, env: TypeResolverEnvironment): GraphQLType? =
            if (!kClass.isEnum()) {
                null
            } else {
                val name = kClass.getNameOrThrow()

                env.cache.getCachedOrElsePut(name) {
                    GraphQLEnumType.newEnum()
                            .name(name.name)
                            .also { builder ->
                                kClass.java.enumConstants.forEach {
                                    it as Enum<*>
                                    builder.value(it.name, it.ordinal)
                                }
                            }
                            .build()
                }
            }
}