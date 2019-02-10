package giraffeql.resolver

import giraffeql.extensions.*
import graphql.schema.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class ObjectTypeResolver : TypeResolver {

    override fun resolve(kClass: KClass<*>, env: TypeResolverEnvironment): GraphQLType? =
            if (!kClass.isClass()) {
                null
            } else {
                val kClassName = kClass.getNameOrThrow()

                env.cache.getCachedOrElsePut(kClassName) {
                    GraphQLObjectType.newObject()
                            .name(kClassName.name)
                            .also { builder ->
                                kClass.memberProperties.forEach { prop ->
                                    env.resolver.resolve(prop.returnType, env).also {
                                        builder.field(toField(kClassName, prop, it?.wrapNonNull(prop.returnType)))
                                    }
                                }
                            }
                            .build()
                }
            }

    private fun toField(kClassName: KClassName, prop: KProperty1<*, *>, result: GraphQLType?): GraphQLFieldDefinition =
            result?.let {
                when {
                    it.allOutputTypes() -> GraphQLFieldDefinition.newFieldDefinition()
                            .name(prop.name)
                            .type(result as GraphQLOutputType)
                            .build()
                    it.allInputTypes() -> throw TypeResolverMismatchException("Expected '${kClassName.name}.${prop.name}' to contain all GraphQLOutputType(s) " +
                            "but found GraphQLInputType(s)")
                    else -> null
                }
            } ?: throw TypeResolverException(path = "${kClassName.name}.${prop.name}")

}
