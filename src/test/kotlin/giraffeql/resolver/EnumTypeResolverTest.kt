package giraffeql.resolver

import giraffeql.extensions.KClassName
import graphql.schema.GraphQLTypeUtil
import graphql.schema.idl.SchemaPrinter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class EnumTypeResolverTest {

    @Test
    fun `return null when no resolver matches`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(EnumTypeResolver())))

        data class TestEnum(val x: String)

        assertThat(env.resolver.resolve(TestEnum::class, env)).isNull()
        assertThat(env.resolver.resolve(TestEnum::class, getProperty(TestEnum::class, "x"), env)).isNull()
        assertThat(env.resolver.resolve(getProperty(TestEnum::class, "x").returnType, env)).isNull()
    }

    enum class TestEnum {
        A, B, C
    }

    @Test
    fun `resolve enum for KClass`() {
        val env = mockEnvironment(resolver = EnumTypeResolver())

        env.resolver.resolve(TestEnum::class, env).also { actual ->
            assertNotNull(actual)

            val expect = """
                enum TestEnum {
                  A
                  B
                  C
                }
            """.trimIndent()

            assertThat(SchemaPrinter().print(actual).trim()).isEqualTo(expect)
        }
    }

    @Test
    fun `resolve enum for KType`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(EnumTypeResolver())))

        data class TestEnumProp(val x: TestEnum)

        env.resolver.resolve(getProperty(TestEnumProp::class, "x").returnType, env).also { actual ->
            assertNotNull(actual)

            val expect = """
                enum TestEnum {
                  A
                  B
                  C
                }
            """.trimIndent()

            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(expect)
        }
    }

    @Test
    fun `resolve enum for KProperty`() {
        val env = mockEnvironment(resolver = ComposedTypeResolver(listOf(EnumTypeResolver())))

        data class TestEnumProp(val x: TestEnum)

        env.resolver.resolve(TestEnumProp::class, getProperty(TestEnumProp::class, "x"), env).also { actual ->
            assertNotNull(actual)

            val expect = """
                enum TestEnum {
                  A
                  B
                  C
                }
            """.trimIndent()

            assertThat(SchemaPrinter().print(GraphQLTypeUtil.unwrapOne(actual)).trim()).isEqualTo(expect)
        }
    }

    @Test
    fun `adds to cache on first access`() {
        val env = mockEnvironment(resolver = EnumTypeResolver())

        env.resolver.resolve(TestEnum::class, env)

        assertThat(KClassName("TestEnum") in env.cache).isTrue()
        assertThat(env.cache.getEntry(KClassName("TestEnum"))?.hits).isEqualTo(0)
    }

    @Test
    fun `gets from cache after first access`() {
        val env = mockEnvironment(resolver = EnumTypeResolver())

        // 0 hits on first access
        env.resolver.resolve(TestEnum::class, env)
        // 1 hit
        env.resolver.resolve(TestEnum::class, env)
        // 2 hits
        env.resolver.resolve(TestEnum::class, env)
        // 3 hits
        env.resolver.resolve(TestEnum::class, env)

        assertThat(KClassName("TestEnum") in env.cache).isTrue()
        assertThat(env.cache.getEntry(KClassName("TestEnum"))?.hits).isEqualTo(3)
    }
}