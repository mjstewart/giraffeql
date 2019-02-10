package giraffeql.resolver

import giraffeql.extensions.KClassName
import giraffeql.extensions.TypeResolverMismatchException
import graphql.schema.GraphQLTypeUtil
import graphql.schema.idl.SchemaPrinter
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ObjectTypeResolverTest {

    @Test
    fun `return null on non matching KClass`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ObjectTypeResolver()))
        )

        env.resolver.resolve(Int::class, env).also { actual ->
             assertNull(actual)
        }
    }

    @Test
    fun `return null on non matching KType`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ObjectTypeResolver()))
        )

        data class Test(val x: String)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNull(actual)
        }
    }

    @Test
    fun `resolve for KClass`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: String)

        env.resolver.resolve(Test::class, env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(actual).trim()).isEqualTo(
                    """
                        type Test {
                          x: String!
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `resolve for KType`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class PropertyTest(val y: String)
        data class Test(val x: PropertyTest)

        env.resolver.resolve(getPropertyType(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(
                    """
                        type PropertyTest {
                          y: String!
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `TypeResolverMismatchException when output type contains a field having an input type at any depth`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ObjectTypeResolver(), ScalarTypeResolver(), ListLikeTypeResolver()))
        )

        data class TestInput(val y: Int)
        data class Test(val x: List<TestInput>)

        Assertions.assertThatThrownBy { env.resolver.resolve(Test::class, env) }
                .isExactlyInstanceOf(TypeResolverMismatchException::class.java)
                .hasMessage("Expected 'Test.x' to contain all GraphQLOutputType(s) but found GraphQLInputType(s)")
    }

    @Test
    fun `adds to cache on first access`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: String)

        env.resolver.resolve(Test::class, env)

        assertThat(KClassName("Test") in env.cache).isTrue()
        assertThat(env.cache.getEntry(KClassName("Test"))?.hits).isEqualTo(0)
    }

    @Test
    fun `gets from cache after first access`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(ObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class Test(val x: String)

        // 0 hits on first access
        env.resolver.resolve(Test::class, env)
        // 1 hit
        env.resolver.resolve(Test::class, env)
        // 2 hits
        env.resolver.resolve(Test::class, env)
        // 3 hits
        env.resolver.resolve(Test::class, env)

        assertThat(KClassName("Test") in env.cache).isTrue()
        assertThat(env.cache.getEntry(KClassName("Test"))?.hits).isEqualTo(3)
    }
}