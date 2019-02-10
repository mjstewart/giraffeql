package giraffeql.resolver

import giraffeql.extensions.isListLike
import giraffeql.extensions.wrapNonNull
import graphql.schema.GraphQLList
import graphql.schema.GraphQLType
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

class ListLikeTypeResolver : TypeResolver {

    override fun resolve(type: KType, env: TypeResolverEnvironment): GraphQLType? =
            if (!type.isListLike()) null else
                type.arguments.firstOrNull()?.let { projection ->
                    if (projection == KTypeProjection.STAR) null else
                        when (val nextType = projection.type) {
                            null -> env.resolver.resolve(type, env)?.wrapNonNull(type)
                            else -> env.resolver.resolve(nextType, env)?.let { GraphQLList.list(it).wrapNonNull(type) }
                        }
                }
}
