package giraffeql.resolver

import giraffeql.configuration.SchemaConfiguration
import giraffeql.extensions.KClassName
import giraffeql.extensions.TypeResolverMismatchException
import graphql.schema.GraphQLTypeUtil
import graphql.schema.idl.SchemaPrinter
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class InputObjectTypeResolverTest {

    @Test
    fun `return null when no resolver matches`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver())))

        data class Test(val x: String)

        assertThat(env.resolver.resolve(Test::class, env)).isNull()
        assertThat(env.resolver.resolve(getProperty(Test::class, "x").returnType, env)).isNull()
        assertThat(env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env)).isNull()
    }

    @Test
    fun `resolve class ending with Input by convention for KClass`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class PropertyTestInput(val y: String)
        data class TestInput(val x: String, val y: PropertyTestInput)

        env.resolver.resolve(TestInput::class, env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(actual).trim()).isEqualTo(
                    """
                        input TestInput {
                          x: String!
                          y: PropertyTestInput!
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `resolve class ending with Input by convention for KType`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class AnotherPropertyInput(val z: String)
        data class PropertyTestInput(val y: String, val z: AnotherPropertyInput)
        data class TestInput(val x: PropertyTestInput)

        env.resolver.resolve(getProperty(TestInput::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(
                    """
                        input PropertyTestInput {
                          y: String!
                          z: AnotherPropertyInput!
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `resolve class ending with Input by convention for KProperty`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class AnotherPropertyInput(val z: String)
        data class PropertyTestInput(val y: String, val z: AnotherPropertyInput)
        data class TestInput(val x: PropertyTestInput)

        env.resolver.resolve(TestInput::class, getProperty(TestInput::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(
                    """
                        input PropertyTestInput {
                          y: String!
                          z: AnotherPropertyInput!
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `resolve class ending with Input by convention for nullable KClass`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class PropertyTestInput(val y: String)
        data class TestInput(val x: String?, val y: PropertyTestInput?)

        env.resolver.resolve(TestInput::class, env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(actual).trim()).isEqualTo(
                    """
                        input TestInput {
                          x: String
                          y: PropertyTestInput
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `resolve class ending with Input by convention for nullable KType`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class AnotherPropertyInput(val z: String?)
        data class PropertyTestInput(val y: String?, val z: AnotherPropertyInput?)
        data class TestInput(val x: PropertyTestInput?)

        env.resolver.resolve(getProperty(TestInput::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(
                    """
                        input PropertyTestInput {
                          y: String
                          z: AnotherPropertyInput
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `resolve class ending with Input by convention for nullable KProperty`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class AnotherPropertyInput(val z: String?)
        data class PropertyTestInput(val y: String?, val z: AnotherPropertyInput?)
        data class TestInput(val x: PropertyTestInput?)

        env.resolver.resolve(TestInput::class, getProperty(TestInput::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(
                    """
                        input PropertyTestInput {
                          y: String
                          z: AnotherPropertyInput
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `uses schema configuration to identify matching input KClass`() {
        val config = object : SchemaConfiguration {
            override fun isInputType(kClass: KClass<*>): Boolean = kClass.simpleName?.endsWith("ABC") ?: false
        }

        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver())),
                config = config
        )

        data class TestABC(val x: String)

        env.resolver.resolve(TestABC::class, env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(actual).trim()).isEqualTo(
                    """
                        input TestABC {
                          x: String!
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `uses schema configuration to identify matching input KType`() {
        val config = object : SchemaConfiguration {
            override fun isInputType(kClass: KClass<*>): Boolean = kClass.simpleName?.endsWith("In") ?: false
        }

        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver())),
                config = config
        )

        data class TestBIn(val y: Int)
        data class Test(val x: TestBIn)

        env.resolver.resolve(getProperty(Test::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(
                    """
                        input TestBIn {
                          y: Int!
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `uses schema configuration to identify matching input KProperty`() {
        val config = object : SchemaConfiguration {
            override fun isInputType(kClass: KClass<*>): Boolean = kClass.simpleName?.endsWith("In") ?: false
        }

        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver())),
                config = config
        )

        data class TestBIn(val y: Int)
        data class Test(val x: TestBIn)

        env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env).also { actual ->
            assertNotNull(actual)
            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(
                    """
                        input TestBIn {
                          y: Int!
                        }
                    """.trimIndent()
            )
        }
    }

    @Test
    fun `TypeResolverMismatchException when input type contains a field having an output type at any depth`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ObjectTypeResolver(), ScalarTypeResolver(), ListLikeTypeResolver()))
        )

        data class TestOutputType(val y: Int)
        data class TestInput(val x: List<TestOutputType>)

        assertThatThrownBy { env.resolver.resolve(TestInput::class, env) }
                .isExactlyInstanceOf(TypeResolverMismatchException::class.java)
                .hasMessage("Expected 'TestInput.x' to contain all GraphQLInputType(s) but found GraphQLOutputType(s)")
    }

    @Test
    fun `adds to cache on first access`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class TestInput(val x: String)

        env.resolver.resolve(TestInput::class, env)

        assertThat(KClassName("TestInput") in env.cache).isTrue()
        assertThat(env.cache.getEntry(KClassName("TestInput"))?.hits).isEqualTo(0)
    }

    @Test
    fun `gets from cache after first access`() {
        val env = mockEnvironment(
                resolver = ComposedTypeResolver(listOf(InputObjectTypeResolver(), ScalarTypeResolver()))
        )

        data class TestInput(val x: String)

        // 0 hits on first access
        env.resolver.resolve(TestInput::class, env)
        // 1 hit
        env.resolver.resolve(TestInput::class, env)
        // 2 hits
        env.resolver.resolve(TestInput::class, env)
        // 3 hits
        env.resolver.resolve(TestInput::class, env)

        assertThat(KClassName("TestInput") in env.cache).isTrue()
        assertThat(env.cache.getEntry(KClassName("TestInput"))?.hits).isEqualTo(3)
    }
}