package giraffeql.resolver

import graphql.schema.GraphQLTypeUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.test.assertNotNull

class ScalarTypeResolverTest {

    private inline fun <reified T> runResolverTest(root: KClass<*>, type: T, expect: String) {
        val env = mockEnvironment(resolver = ScalarTypeResolver())

        when (type) {
            is KType ->
                env.resolver.resolve(type, env).also { actual ->
                    assertNotNull(actual)
                    assertThat(GraphQLTypeUtil.simplePrint(actual))
                            .withFailMessage("KType resolver: expect <%s>, got: <%s>", expect, actual)
                            .isEqualTo(expect)
                }
            is KProperty1<*, *> ->
                env.resolver.resolve(root, type, env).also { actual ->
                    assertNotNull(actual)
                    assertThat(GraphQLTypeUtil.simplePrint(actual))
                            .withFailMessage("KProperty resolver: expect <%s>, got: <%s>", expect, actual)
                            .isEqualTo(expect)
                }
            else -> throw IllegalStateException("Unhandled type provided to when expression")
        }
    }

    @Test
    fun `return null when no resolver matches`() {
        val env = mockEnvironment(resolver = ScalarTypeResolver())

        data class NonScalarType(val y: String)
        data class Test(val x: NonScalarType)

        assertThat(env.resolver.resolve(Test::class, env)).isNull()
        assertThat(env.resolver.resolve(getProperty(Test::class, "x").returnType, env)).isNull()
        assertThat(env.resolver.resolve(Test::class, getProperty(Test::class, "x"), env)).isNull()
    }

    @Test
    fun `String to GraphQLString`() {
        data class TestNullable(val x: String?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "String")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "String")

        data class TestNonNullable(val x: String)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "String!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "String!")
    }


    @Test
    fun `Boolean to GraphQLBoolean`() {
        data class TestNullable(val x: Boolean?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "Boolean")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "Boolean")

        data class TestNonNullable(val x: Boolean)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "Boolean!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "Boolean!")
    }

    @Test
    fun `Int to GraphQLInt`() {
        data class TestNullable(val x: Int?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "Int")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "Int")

        data class TestNonNullable(val x: Int)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "Int!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "Int!")
    }

    @Test
    fun `Float to GraphQLFloat`() {
        data class TestNullable(val x: Float?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "Float")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "Float")

        data class TestNonNullable(val x: Float)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "Float!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "Float!")
    }

    @Test
    fun `Double to GraphQLFloat`() {
        data class TestNullable(val x: Double?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "Float")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "Float")

        data class TestNonNullable(val x: Double)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "Float!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "Float!")
    }

    @Test
    fun `Long to GraphQLLong`() {
        data class TestNullable(val x: Long?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "Long")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "Long")

        data class TestNonNullable(val x: Long)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "Long!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "Long!")
    }

    @Test
    fun `BigDecimal to GraphQLBigDecimal`() {
        data class TestNullable(val x: BigDecimal?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "BigDecimal")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "BigDecimal")

        data class TestNonNullable(val x: BigDecimal)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "BigDecimal!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "BigDecimal!")
    }

    @Test
    fun `BigInteger to GraphQLBigInteger`() {
        data class TestNullable(val x: BigInteger?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "BigInteger")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "BigInteger")

        data class TestNonNullable(val x: BigInteger)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "BigInteger!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "BigInteger!")
    }

    @Test
    fun `Byte to GraphQLByte`() {
        data class TestNullable(val x: Byte?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "Byte")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "Byte")

        data class TestNonNullable(val x: Byte)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "Byte!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "Byte!")
    }

    @Test
    fun `Char to GraphQLChar`() {
        data class TestNullable(val x: Char?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "Char")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "Char")

        data class TestNonNullable(val x: Char)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "Char!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "Char!")
    }

    @Test
    fun `Short to GraphQLShort`() {
        data class TestNullable(val x: Short?)
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x").returnType, "Short")
        runResolverTest(TestNullable::class, getProperty(TestNullable::class, "x"), "Short")

        data class TestNonNullable(val x: Short)
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x").returnType, "Short!")
        runResolverTest(TestNonNullable::class, getProperty(TestNonNullable::class, "x"), "Short!")
    }
}