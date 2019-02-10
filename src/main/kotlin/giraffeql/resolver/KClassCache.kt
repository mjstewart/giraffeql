package giraffeql.resolver

import giraffeql.extensions.KClassName
import graphql.schema.GraphQLType

data class CacheEntry(val value: GraphQLType, val hits: Long = 0)

class KClassCache(private val cache: MutableMap<KClassName, CacheEntry> = mutableMapOf()) {

//    fun getCachedOrElsePut(kClassName: KClassName, producer: () -> GraphQLType): GraphQLType =
//            cache[kClassName].let { it?.value ?: producer().apply { cache[kClassName] = CacheMetric(this) } }

    fun getCachedOrElsePut(kClassName: KClassName, producer: () -> GraphQLType): GraphQLType =
            cache[kClassName]
                    ?.also { cache[kClassName] = it.copy(hits = it.hits + 1) }
                    ?.let { it.value }
                    ?: producer().apply { cache[kClassName] = CacheEntry(this) }


    operator fun contains(kClassName: KClassName): Boolean = kClassName in cache

    fun getEntry(kClassName: KClassName): CacheEntry? = cache[kClassName]
}
