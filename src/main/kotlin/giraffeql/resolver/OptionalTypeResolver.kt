package giraffeql.resolver

import giraffeql.extensions.removeNonNull
import graphql.schema.GraphQLType
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class OptionalTypeResolver : TypeResolver {

    override fun resolve(type: KType, env: TypeResolverEnvironment): GraphQLType? =
            if (!type.jvmErasure.isSubclassOf(Optional::class)) null else
                type.arguments.firstOrNull()?.let {
                    when (val nextType = it.type) {
                        null -> null
                        else -> env.resolver.resolve(nextType, env)?.removeNonNull()
                    }
                }
}
