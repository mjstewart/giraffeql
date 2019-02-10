package giraffeql.resolver

import giraffeql.extensions.*
import graphql.schema.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

class IDTypeResolver : TypeResolver {

    companion object {
        private val supportedIDs: Set<KClass<*>> = setOf(Int::class, Long::class, String::class, UUID::class)

        fun isSupportedID(kClass: KClass<*>): Boolean = kClass in supportedIDs
    }

    override fun resolve(type: KType, env: TypeResolverEnvironment): GraphQLType? = null
}
