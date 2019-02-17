package giraffeql.resolver

import giraffeql.extensions.wrapNonNull
import graphql.Scalars
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ScalarTypeResolver : TypeResolver {

    companion object {
        private val mappings: Map<KClass<*>, GraphQLScalarType> = mapOf(
                String::class to Scalars.GraphQLString,
                Boolean::class to Scalars.GraphQLBoolean,
                Int::class to Scalars.GraphQLInt,
                Float::class to Scalars.GraphQLFloat,
                Double::class to Scalars.GraphQLFloat,
                Long::class to Scalars.GraphQLLong,
                BigDecimal::class to Scalars.GraphQLBigDecimal,
                BigInteger::class to Scalars.GraphQLBigInteger,
                Byte::class to Scalars.GraphQLByte,
                Char::class to Scalars.GraphQLChar,
                Short::class to Scalars.GraphQLShort
        )

        fun isSupportedScalar(kClass: KClass<*>): Boolean = kClass in mappings
    }

    override fun resolve(type: KType, env: TypeResolverEnvironment): GraphQLType? = mappings[type.jvmErasure]?.wrapNonNull(type)
}
