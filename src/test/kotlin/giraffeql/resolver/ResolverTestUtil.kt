package giraffeql.resolver

import giraffeql.configuration.SchemaConfiguration
import graphql.Scalars
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import io.mockk.every
import io.mockk.mockk
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

fun mockEnvironment(
        resolver: TypeResolver = object : TypeResolver {},
        config: SchemaConfiguration = object : SchemaConfiguration {}
) = TypeResolverEnvironment(resolver, config)

fun getProperty(kClass: KClass<*>, key: String): KProperty1<*, *> = kClass.memberProperties.first { it.name == key }

fun extractFirstPropName(kClass: KClass<*>): String = extractFirstProp(kClass).name

fun extractFirstProp(kClass: KClass<*>): KProperty1<*, *> = kClass.memberProperties.first()