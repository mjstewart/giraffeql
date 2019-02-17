package giraffeql.resolver

import graphql.schema.GraphQLTypeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ListLikeTypeResolverTest {

    @Test
    fun `return null when no resolver matches`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver()))
        )

        data class Test(val x: String)

        assertThat(env.resolver.resolve(Test::class, env)).isNull()
        assertThat(env.resolver.resolve(getProperty(Test::class, "x").returnType, env)).isNull()
        assertThat(env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env)).isNull()
    }

    @Test
    fun `star projection should return null given its not resolvable to a concrete gql type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver()))
        )

        data class Test(val x: List<*>)

        assertThat(env.resolver.resolve(getProperty(Test::class, "x").returnType, env)).isNull()
        assertThat(env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env)).isNull()
    }

    @Test
    fun `generic type should return null given its not resolvable to a concrete gql type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver()))
        )

        data class Test<T>(val x: List<T>)

        assertThat(env.resolver.resolve(getProperty(Test::class, "x").returnType, env)).isNull()
        assertThat(env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env)).isNull()
    }

    @Test
    fun `nullable List type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: List<String?>?)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String]")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String]")
        }
    }

    @Test
    fun `List type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: List<String>)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String!]!")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String!]!")
        }
    }

    @Test
    fun `nullable nested List type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: List<List<List<List<String?>?>?>?>?)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String]]]]")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String]]]]")
        }
    }

    @Test
    fun `nested List type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: List<List<List<List<String>>>>)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String!]!]!]!]!")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String!]!]!]!]!")
        }
    }

    @Test
    fun `nullable Array type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<String?>?)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String]")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String]")
        }
    }

    @Test
    fun `Array type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<String>)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String!]!")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[String!]!")
        }
    }

    @Test
    fun `nested nullable Array type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<Array<Array<Array<String?>?>?>?>?)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String]]]]")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String]]]]")
        }
    }

    @Test
    fun `nested Array type`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ListLikeTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(@Suppress("ArrayInDataClass") val x: Array<Array<Array<Array<String>>>>)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[String!]!]!]!]!")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
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

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[Person]]]]")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
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

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[Person!]!]!]!]!")
        }
        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(GraphQLTypeUtil.simplePrint(actual)).isEqualTo("[[[[Person!]!]!]!]!")
        }
    }
}