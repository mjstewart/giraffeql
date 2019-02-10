package giraffeql.resolver

import giraffeql.configuration.SchemaConfiguration
import graphql.Scalars
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import io.mockk.every
import io.mockk.mockk
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure


fun mockEnvironment(
        resolver: TypeResolver = object : TypeResolver {},
        config: SchemaConfiguration = object : SchemaConfiguration {}
) = TypeResolverEnvironment(resolver, config)

fun getPropertyType(kClass: KClass<*>, key: String): KType = kClass.memberProperties.first { it.name == key }.returnType
