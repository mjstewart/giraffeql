package giraffeql.resolver

import giraffeql.extensions.KClassName
import graphql.schema.GraphQLTypeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ListLikeTypeResolverTest {

    @Test
    fun `return null on non matching list like KClass`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver()))
        )

        data class Test(val x: String)

        env.resolver.resolve(Test::class, env).also { actual ->
            assertNull(actual)
        }
    }

    @Test
    fun `return null on non matching list like KType`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver()))
        )

        data class Test(val x: String)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNull(actual)
        }
    }

    @Test
    fun `star projection should return null given its not resolvable to a concrete gql type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver()))
        )

        data class Test(val x: List<*>)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNull(actual)
        }
    }

    @Test
    fun `generic type should return null given its not resolvable to a concrete gql type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver()))
        )

        data class Test<T>(val x: List<T>)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNull(actual)
        }
    }

    @Test
    fun `single nested nullable List type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: List<String?>?)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String]")
        }
    }

    @Test
    fun `single nested List type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: List<String>)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String!]!")
        }
    }

    @Test
    fun `multiple nested nullable List type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: List<List<List<List<String?>?>?>?>?)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String]]]]")
        }
    }

    @Test
    fun `multiple nested List type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: List<List<List<List<String>>>>)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String!]!]!]!]!")
        }
    }

    @Test
    fun `single nested nullable Array type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<String?>?)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String]")
        }
    }

    @Test
    fun `single nested Array type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<String>)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String!]!")
        }
    }

    @Test
    fun `multiple nested nullable Array type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<Array<Array<Array<String?>?>?>?>?)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String]]]]")
        }
    }

    @Test
    fun `multiple nested Array type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<Array<Array<Array<String>>>>)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String!]!]!]!]!")
        }
    }

    data class Person(val x: String)

    @Test
    fun `mix nested nullable types`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<List<Array<List<Person?>?>?>?>?)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[Person]]]]")
        }
    }

    @Test
    fun `mix nested typed`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<List<Array<List<Person>>>>)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[Person!]!]!]!]!")
        }
    }
}