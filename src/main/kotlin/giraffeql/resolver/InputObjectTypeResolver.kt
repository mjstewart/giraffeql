package giraffeql.resolver

import giraffeql.extensions.*
import graphql.schema.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class InputObjectTypeResolver : TypeResolver {

    override fun resolve(kClass: KClass<*>, env: TypeResolverEnvironment): GraphQLType? =
            if (!env.config.isInputType(kClass)) {
                null
            } else {
                val kClassName = kClass.getNameOrThrow()

                env.cache.getCachedOrElsePut(kClassName) {
                    GraphQLInputObjectType.newInputObject()
                            .name(kClassName.name)
                            .also { builder ->
                                kClass.memberProperties.forEach { prop ->
                                    env.resolver.resolve(kClass, prop, env).also {
                                        builder.field(toField(kClassName, prop, it?.wrapNonNull(prop.returnType)))
                                    }
                                }
                            }
                            .build()
                }
            }

    private fun toField(kClassName: KClassName, prop: KProperty1<*, *>, result: GraphQLType?): GraphQLInputObjectField =
            result?.let {
                when {
                    it.allInputTypes() ->GraphQLInputObjectField.newInputObjectField()
                            .name(prop.name)
                            .type(result as GraphQLInputType)
                            .build()
                    it.allOutputTypes() -> throw TypeResolverMismatchException("Expected '${kClassName.name}.${prop.name}' to contain all GraphQLInputType(s) " +
                            "but found GraphQLOutputType(s)")
                    else -> null
                }
            } ?: throw TypeResolverException(path = "${kClassName.name}.${prop.name}")
}
